package pass.passholder.service;

import org.springframework.stereotype.Service;
import pass.passholder.entity.AppUser;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class AESUtil {
    private Map<String, IvParameterSpec> ivParameterSpecMap = new HashMap<>();
    private Map<String, SecretKey> secretKeyMap = new HashMap<>();

    public AESUtil(){
    }

    private SecretKey getSecretKey(String username){
        if(secretKeyMap.get(username) != null){
            return secretKeyMap.get(username);
        }else {
            try {
                generateKey(username);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return secretKeyMap.get(username);
        }
    }

    private IvParameterSpec getIv(String username){
        if(ivParameterSpecMap.get(username) != null){
            return ivParameterSpecMap.get(username);
        }
        generateIv(username);
        return ivParameterSpecMap.get(username);
    }

    public String encrypt(String input, String username) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        SecretKey key = getSecretKey(username);
        IvParameterSpec iv = getIv(username);
        String algorithm = "AES/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes("UTF-8")));

    }

    public String decrypt(String cipherText, String username) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey key = getSecretKey(username);
        IvParameterSpec iv = getIv(username);
        String algorithm = "AES/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    private void generateKey(String username) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        secretKeyMap.put(username, key);
    }

    private void generateIv(String username) {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        ivParameterSpecMap.put(username, new IvParameterSpec(iv));
    }
}
