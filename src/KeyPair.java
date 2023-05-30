import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class KeyPair {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String public_name, private_name;
        KeyPairGenerator keyPairGen;
//        key 생성
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(1024);
        java.security.KeyPair keyPair = keyPairGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.print("공개키를 저장할 파일이름 : ");
        public_name = sc.next();

        System.out.print("개인키를 저장할 파일이름 : ");
        private_name = sc.next();



//        공개키
        try(FileOutputStream fileOutputStream = new FileOutputStream(public_name)){
            try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){
                objectOutputStream.writeObject(publicKey.getEncoded());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
//        개인키
        try(FileOutputStream fileOutputStream = new FileOutputStream(private_name)){
            try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){
                objectOutputStream.writeObject(privateKey.getEncoded());
            }
        }catch (IOException e){
            e.printStackTrace();
        }    }

}
