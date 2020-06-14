package com.hongjun.controller;

import com.alibaba.druid.util.StringUtils;
import com.hongjun.controller.viewobject.UserVO;
import com.hongjun.error.BusinessException;
import com.hongjun.error.EmBusinessError;
import com.hongjun.response.CommonReturnType;
import com.hongjun.service.UserService;
import com.hongjun.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;
import sun.security.krb5.internal.PAData;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author hongjun500
 * @date 2020/6/13 22:54
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Controller
@RequestMapping("/user")
// 实现跨域请求，(此处引入了thymeleaf，加入到了项目里面以请求的方式访问(页面)，不做处理也行的通，但是如果前后端分离则需要用到）
@CrossOrigin
public class UserController extends BaseController{

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;


    /**
     * 此处使用spring容器的方式注入HttpServletRequest，对应当前用户的访问，支持并发
     */
    @Autowired
    private HttpServletRequest request;

    /**
     * 获取短信页面
     * @return
     */
    @GetMapping(value = "/otpView")
    public String otpView(){
        return "getOtp";
    }

    /**
     * 注册页面
     * @return
     */
    @GetMapping(value = "/registerView")
    public String registerView(){
        return "register";
    }

    /**
     * 注册接口
     * @return
     */
    @PostMapping(value = "/register")
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password,
                                     @RequestParam(name = "gender") Byte gender) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 验证对应手机号和对应的otpCode是否符合
        // 从session中获取键为telphone的值 对应91，92行
        String inSessionOtpCode = String.valueOf(request.getSession().getAttribute(telphone));
        // 比较用户输入的otpCode和之前通过手机号发送绑定的存储在session中的otpCode
        if (!StringUtils.equals(otpCode, inSessionOtpCode)){
            // 这里的比较是使用alibaba-druid类库，里面做了判空,内部实现如下
            /*public static boolean equals(String a, String b) {
                if (a == null) {
                    return b == null;
                } else {
                    return a.equals(b);
                }
            }*/
           throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");

        }

        // 用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byTelPhone");
        // 对密码加密
        userModel.setEncrptPassword(enCodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String enCodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定一个计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder=new BASE64Encoder();
        //加密字符串
        String pwd= base64Encoder.encode(md5.digest(str.getBytes("UTF-8")));
        return pwd;
    }

    /**
     * 这种方式可以解密出来
     * @param source
     * @return
     */
    public static String MD5encode(String source) {
        if (org.apache.commons.lang3.StringUtils.isBlank(source)) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }
        byte[] encode = messageDigest.digest(source.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte anEncode : encode) {
            String hex = Integer.toHexString(0xff & anEncode);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @PostMapping(value = "/getOtp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone){
        // 按照一定的规则生成otp验证码
        Random random = new Random();

        // 此时取值为[0,899999);
        int randomInt = random.nextInt(899999);
        // 此时取值为[100000,999999);
        randomInt += 100000;
        String otpCode = String.valueOf(randomInt);

        // 将otp验证码同对应用户的手机号关联,此处使用HttpSession绑定用户手机号和otpCode (最佳效果为使用Redis)
        // 键为telphone,值为otpCode
        request.getSession().setAttribute(telphone, otpCode);

        // 将otp验证码通过第三方短信通道发送给用户()
        // 此处省略，直接以打印到控制台的方式
        logger.info("otp短信验证码: {}, telphone: {}", otpCode, telphone);
        System.out.println("otp短信验证码: " + otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/get/{id}")
    @ResponseBody
    public CommonReturnType getUser(@PathVariable(value = "id") Integer id) throws BusinessException {
        // 调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        // 若对象不存在
        if (userModel == null){
            // userModel.setEncrptPassword("fdfdsfsd");
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        // 将核心领域模型的对象返回给前端UI
        UserVO userVO = convertFromModel(userModel);
        // 返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
}
