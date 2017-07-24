package com.zhbit.car.util;
import com.zhbit.car.model.User;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * Created by admin on 2017/7/20.
 */
public class PasswordHelper {
    //private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
    private String algorithmName = "md5";
    private int hashIterations = 2;

    public void encryptPassword(User user) {
        //String salt=randomNumberGenerator.nextBytes().toHex();
        String newPassword = new SimpleHash(algorithmName, user.getPassword(),  ByteSource.Util.bytes(user.getUsername()), hashIterations).toHex();
        //String newPassword = new SimpleHash(algorithmName, user.getPassword()).toHex();
        user.setPassword(newPassword);

    }
    public static void main(String[] args) {
        PasswordHelper passwordHelper = new PasswordHelper();
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        passwordHelper.encryptPassword(user);
        System.out.println(user.getPassword());
    }
}
