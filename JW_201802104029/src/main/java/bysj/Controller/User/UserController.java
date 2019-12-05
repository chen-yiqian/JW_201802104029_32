package bysj.Controller.User;

import bysj.Daomain.User;
import bysj.Service.UserService;
import bysj.Util.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/user.ctl")
public class UserController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //读取参数id
        String id_str = request.getParameter("id");
        try {
            //如果id = null, 表示响应所有职称对象，否则响应id指定的职称对象
            if (id_str == null) {
                //向客户端响应所有信息
                responseUsers(response);
            } else {
                int id = Integer.parseInt(id_str);
                //响应id的那一行数据
                responseUser(id, response);
            }
        }catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("列表失败，数据库操作异常");
        } catch(Exception e){
            e.printStackTrace();
            response.getWriter().println("列表失败，网络异常");
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user_json = JSONUtil.getJSON(request);
        //将JSON字串解析为user对象
        User userToAdd = JSON.parseObject(user_json, User.class);
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //增加user对象
        try {
            boolean update= UserService.getInstance().update(userToAdd);
            if(update){
                //加入数据信息
                message.put("statusCode", "200");
                message.put("message", "更新成功");
            }else {
                message.put("message","更新失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            message.put("message", "更新失败，数据库操作异常");
        } catch(Exception e){
            e.printStackTrace();
            message.put("message", "更新失败，网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }
    //响应一个用户对象
    private void responseUser(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找用户
        User user = UserService.getInstance().find(id);
        String user_json = JSON.toJSONString(user);
        //响应
        response.getWriter().println(user_json);
    }

    //响应所有用户对象
    private void responseUsers(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有user
        Collection<User> users = UserService.getInstance().findAll();
        String users_json = JSON.toJSONString(users);
        //响应
        response.getWriter().println(users_json);
    }
}
