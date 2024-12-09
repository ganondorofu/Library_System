package com.example.demo.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class MfaUtil {

    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public static String generateSecretKey() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static boolean verifyCode(String secretKey, int verificationCode) {
        return gAuth.authorize(secretKey, verificationCode);
    }

    public static String generateQRCodeBase64(String barCodeData) {
        try {
            BitMatrix matrix = new com.google.zxing.qrcode.QRCodeWriter()
                    .encode(barCodeData, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }
}
