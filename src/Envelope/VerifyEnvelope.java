package Envelope;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
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
        String envelopeName;

        KeyPairGenerator keyPairGen;
        KeyGenerator keyGenerator;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
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
        System.out.print("텍스트 입력(100byte 이하) : ");
        data = scanner.nextLine();
        System.out.print("공개키 파일 입력 : ");
        publicName = scanner.next();

//        bufferData => data의 byte타입으로 변환
        byte[] bufferData = data.getBytes();

//        signature 생성하기
        Signature signature;
        Signature signature_verify;// 검증 signature 객체
        byte[] sign;
        try {
            signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(bufferData);
            sign = signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
        byte[] signatureBuffer = new byte[1000];
//        공개키 읽어들이기
        try (FileInputStream fileInputStream = new FileInputStream(publicName)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                byte[] buffer = (byte[]) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("전자봉투 파일 입력 : ");
        envelopeName = scanner.next();


//      서명 정보 출력하기(복호화 후 출력)
//      복호화
        try (FileInputStream fis = new FileInputStream(envelopeName);
             CipherInputStream cis = new CipherInputStream(fis, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                String decrypted = new String(buffer, 0, bytesRead);
                System.out.print(decrypted);
            }

            // 검증을 위한 Signature 객체 생성
            try {
                signature_verify = Signature.getInstance(signAlgorithm);
                signature_verify.initVerify(publicKey);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }

            // 서명 검증
            try {
                signature_verify.update(signatureBuffer);
                boolean isVerified = signature_verify.verify(signatureBuffer);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
//        서명 검증하기
            try {
                boolean rslt = signature_verify.verify(signatureBuffer);
                System.out.println("서명 검증 결과: " + rslt);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
