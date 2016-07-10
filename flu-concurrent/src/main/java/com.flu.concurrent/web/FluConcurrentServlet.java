package com.flu.concurrent.web;

import com.alibaba.fastjson.JSONObject;
import com.flu.concurrent.stastic.StaInfoCenter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by float.lu on 7/9/16.
 */
public class FluConcurrentServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        String poolKeys = req.getParameter("poolKeys");
        if (poolKeys != null) {
            write(resp, JSONObject.toJSONString(StaInfoCenter.mySelf().getPoolNames()));
            return;
        }
        String json = req.getParameter("json");
        if (json != null) {
            String dateStr = req.getParameter("fromDate");
            if (dateStr != null) {
                write(resp, JSONObject.toJSONString(StaInfoCenter.mySelf().getSeriesFrom(Long.valueOf(dateStr))));
            } else {
                write(resp, JSONObject.toJSONString(StaInfoCenter.mySelf().getSeries()));
            }
            return;
        }
        index(resp);
    }

    private void index(HttpServletResponse resp) throws IOException{
        setHTML(resp);
        PrintWriter writer = resp.getWriter();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("html/flu.html");
        BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = tBufferedReader.readLine()) != null){
            sb.append(line);
        }
        writer.write(sb.toString());
        writer.close();
    }

    private void write(HttpServletResponse resp, String value) throws IOException{
        PrintWriter writer = resp.getWriter();
        writer.write(value);
        writer.close();
    }


    private void setHTML(HttpServletResponse resp) {
        resp.setContentType("text/html; charset=utf-8");
    }

    private void setJson(HttpServletResponse resp) {
        resp.setContentType("application/json; charset=utf-8");
    }
}
