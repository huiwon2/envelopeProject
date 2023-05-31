package Envelope;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.util.Scanner;

public class MakeEnvelope {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String keyAlgorithm = "RSA";
        String signAlgorithm = "SHA256withRSA";
        String data;
        String privateName;

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
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }


        System.out.print("텍스트 입력(100byte 이하) : ");
        data = scanner.nextLine();
        System.out.print("개인키 파일 입력 : ");
        privateName = scanner.next();

//        bufferData => data의 byte타입으로 변환
        byte[] bufferData = data.getBytes();
//        data 읽어들이기(data를 문장으로 받아서)
//        try(FileInputStream fileInputStream = new FileInputStream(data)){
//            try(ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
//                bufferData = (byte[]) objectInputStream.readObject();
//                System.out.println();
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        개인키 읽어들이기
        try(FileInputStream fileInputStream = new FileInputStream(privateName)){
            try(ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
                byte[] buffer = (byte[]) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        signature 생성하기
        Signature signature;
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
//      서명 정보 출력하기
        System.out.println("입력된 서명 정보: " + sign.length);
        for (byte b : sign) {
            System.out.print(String.format("%02x", b) + "\t");
        }
        System.out.println();
        String envelopeFile;
//         저장할 파일 입력받기
        System.out.print("저장할 파일 이름 : ");
        envelopeFile = scanner.next();

//        서명을 대칭키로 암호화하기
        try(FileOutputStream bos = new FileOutputStream(envelopeFile);
            CipherOutputStream cos = new CipherOutputStream(bos, cipher)){
            cos.write(sign);
            cos.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
