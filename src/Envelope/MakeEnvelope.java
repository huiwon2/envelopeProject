package Envelope;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

        try {
            keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(128);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Key secretKey = keyGenerator.generateKey();
        Cipher cipher;

        System.out.print("텍스트 입력(100byte 이하) : ");
        data = scanner.next();
        System.out.print("개인키 파일 입력 : ");
        privateName = scanner.next();

//        bufferData => data의 byte타입으로 변환
        byte[] bufferData;
//        data 읽어들이기(data를 문장으로 받아서)
        try(FileInputStream fileInputStream = new FileInputStream(data)){
            try(ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
                bufferData = (byte[]) objectInputStream.readObject();
                System.out.println();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


    }
}
