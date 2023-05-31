package Envelope;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Scanner;

public class VerifyEnvelope {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String keyAlgorithm = "RSA";
        String signAlgorithm = "SHA256withRSA";
//        원래 데이터 문장을 입력받음
        String data;
        String publicName;

        KeyPairGenerator keyPairGen;
        KeyGenerator keyGenerator;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        //        secretKey 객체 생성
        try {
            keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Key secretKey = keyGenerator.generateKey();
//        cipher 객체
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }
}
