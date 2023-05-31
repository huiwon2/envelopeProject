package Envelope;

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

        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.print("텍스트 입력(100byte 이하) : ");
        data = scanner.next();
//        bufferData => data의 byte타입으로 변환
        byte[] bufferData;
//        data 읽어들이기
        try(FileInputStream fileInputStream = new FileInputStream(data)){
            try(ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
                bufferData = (byte[]) objectInputStream.readObject();
                System.out.println();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
        }

    }
}
