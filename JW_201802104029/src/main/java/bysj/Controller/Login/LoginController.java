package bysj.Controller.Login;

import bysj.Daomain.User;
import bysj.Service.UserService;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        //创建JSON对象，以便于往前端响应信息
        JSONObject message = new JSONObject();
        try{
            //若用户名和密码正确则得到一个代表当前用户的USER对象
            User loggedUser = UserService.getInstance().login(username,password);
            if(loggedUser!=null){
                message.put("message","登陆成功");
                //如果当前请求对应着服务器内存中的一个session对象，则返回该对象
                //如果服务器内存中没有session对象与当前请求对应，创建一个session对象并返回该对象
                HttpSession session =request.getSession();
                //10分钟没有操作，则session失效
                session.setMaxInactiveInterval(10*60);
                //将user对象写入session属性，名为currentUser
                session.setAttribute("currentUser",loggedUser);
                response.getWriter().println(message);
                //此处重定向到索引页（菜单页）
                return;
            }else {
                message.put("message","用户名或密码错误");
            }
        }catch (SQLException e){
            message.put("message","数据库操作异常");
            e.printStackTrace();
        }catch (Exception e){
            message.put("message","网络异常");
            e.printStackTrace();
        }
        response.getWriter().println(message);
    }
}
