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
        String secretName;

        KeyPairGenerator keyPairGen;
        KeyGenerator keyGenerator;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        PublicKey publicKey;
        PrivateKey privateKey = keyPair.getPrivate();

//        secretKey 객체 생성
        try {
            keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Key secretKey;

        System.out.print("텍스트 입력(100byte 이하) : ");
        data = scanner.nextLine();
        System.out.print("공개키 파일 입력 : ");
        publicName = scanner.next();
        System.out.print("대칭키 파일 입력 : ");
        secretName = scanner.next();

//        공개키 읽어들이기
        try(FileInputStream fileInputStream = new FileInputStream(publicName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
            publicKey = (PublicKey) objectInputStream.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        대칭키 읽어들이기
        try(FileInputStream fileInputStream = new FileInputStream(secretName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
            secretKey = (Key) objectInputStream.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

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
//        signature 테스트 출력
        System.out.println("입력된 서명 정보: " + sign.length);
        for (byte b : sign) {
            System.out.print(String.format("%02x", b) + "\t");
        }
        System.out.println();

        byte[] signatureBuffer = new byte[128];

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
        System.out.print("전자봉투 파일 입력 : ");
        envelopeName = scanner.next();

//      서명 정보 출력하기(복호화 후 출력)
//      복호화
        try (FileInputStream fis = new FileInputStream(envelopeName);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[128];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
//                String decrypted = new String(buffer, 0, bytesRead);
//                System.out.print(decrypted);
                baos.write(buffer, 0, bytesRead);
            }
            byte[] decryptedData = baos.toByteArray();
            // 검증을 위한 Signature 객체 생성
            try {
                signature_verify = Signature.getInstance(signAlgorithm);
                signature_verify.initVerify(publicKey);
                signature_verify.update(bufferData);
                System.out.println("입력 결과 : " + signature_verify.verify(sign));
//                (중복코드)boolean isVerified = signature_verify.verify(signatureBuffer);
                System.out.println("입력된 서명 정보: " + signature_verify);
                for (byte b : signature_verify.sign()) {
                    System.out.print(String.format("%02x", b) + "\t");
                }
            }catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }

            // 서명 검증
//            불필요한 try절 나누기
//            try {
//                signature_verify.update(signatureBuffer);
////                (중복코드)boolean isVerified = signature_verify.verify(signatureBuffer);
//                System.out.println("입력된 서명 정보: " + signature_verify.sign().length);
//                for (byte b : signature_verify.sign()) {
//                    System.out.print(String.format("%02x", b) + "\t");
//                }
//            } catch (SignatureException e) {
//                throw new RuntimeException(e);
//            }
//        서명 검증하기
//            try {
//                boolean result = signature_verify.verify(signatureBuffer);
//                System.out.println("서명 검증 결과: " + result);
//            } catch (SignatureException e) {
//                throw new RuntimeException(e);
//            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
