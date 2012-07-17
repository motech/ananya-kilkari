<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>
<%
    boolean fakeTimeAvailable = false;
    String os = (String) System.getenv().get("OS");
    if (os == null || os.indexOf("indows") < 0) {
        try {
            java.lang.reflect.Method m = java.lang.ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, Boolean.TYPE);
            m.setAccessible(true);
            m.invoke(null, java.lang.System.class, "jvmfaketime", false);
            System.registerFakeCurrentTimeMillis();


            
                try {
                    if (request.getMethod() == "POST") {
                        try {
                            String date = request.getParameter("year") + "-" + request.getParameter("month") + "-" +request.getParameter("day");
                            String time = request.getParameter("hour") + ":" + request.getParameter("min") + ":" +request.getParameter("sec");
                            Date dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                            Date timeValue = new SimpleDateFormat("HH:mm:ss").parse(time);
                            dateValue.setHours(timeValue.getHours());
                            dateValue.setMinutes(timeValue.getMinutes());
                            dateValue.setSeconds(timeValue.getSeconds());
                            System.out.println("Posted date " + time.substring(1, time.length() - 1));

                            System.deregisterFakeCurrentTimeMillis();

                            long diffValue = (dateValue.getTime() - System.currentTimeMillis());

                            System.registerFakeCurrentTimeMillis();
                            System.out.println("offset calculated " + diffValue);
                            System.setTimeOffset(diffValue);
                            System.out.println("Date :" + new Date());
                        } catch (java.lang.Exception e) {
                            out.println("Error: " + e.getMessage());
                            return;
                        }
                    } else {
                        String n = request.getParameter("n");
                        if(n != null){
                            Date curDate = new Date(System.currentTimeMillis());
                            if("1".equals(n)) {
                                Date timeValue = new SimpleDateFormat("HH:mm:ss").parse("12:29:00");
                                curDate.setHours(timeValue.getHours());
                                curDate.setMinutes(timeValue.getMinutes());
                                curDate.setSeconds(timeValue.getSeconds());
                            }else if("2".equals(n)){
                                Date timeValue = new SimpleDateFormat("HH:mm:ss").parse("15:29:00");
                                curDate.setHours(timeValue.getHours());
                                curDate.setMinutes(timeValue.getMinutes());
                                curDate.setSeconds(timeValue.getSeconds());
                            }else if("9".equals(n)){
                                Date timeValue = new SimpleDateFormat("HH:mm:ss").parse("09:00:00");
                                curDate.setHours(timeValue.getHours());
                                curDate.setMinutes(timeValue.getMinutes());
                                curDate.setSeconds(timeValue.getSeconds());
                            }else {
                                curDate = new Date(System.currentTimeMillis() + 86400000L);
                                Date timeValue = new SimpleDateFormat("HH:mm:ss").parse("09:00:00");
                                curDate.setHours(timeValue.getHours());
                                curDate.setMinutes(timeValue.getMinutes());
                                curDate.setSeconds(timeValue.getSeconds());
                            }

                            System.deregisterFakeCurrentTimeMillis();

                            long diffValue = (curDate.getTime() - System.currentTimeMillis());

                            System.registerFakeCurrentTimeMillis();
                            System.out.println("offset calculated " + diffValue);
                            System.setTimeOffset(diffValue);
                            System.out.println("Date :" + new Date());
                        }
                    }
                } catch (java.lang.Exception e) {
                    out.println("Error: " + e.getMessage());
                    return;
                }
                fakeTimeAvailable = true;
        } catch (Exception ignore) {
        }
    }

%>
<html>
<head/>
<body>
    Current Time ---- : <%= new Date()%>
    <form action="kilkari-testing-tool.jsp">
        <input type="hidden" name="n" value="0"/>
        <input type="submit" value="Next day"/>
    </form>

    <form action="kilkari-testing-tool.jsp">
        <input type="hidden" name="n" value="9"/>
        <input type="submit" value="Morning 9"/>
    </form>

    <form action="kilkari-testing-tool.jsp">
        <input type="hidden" name="n" value="1"/>
        <input type="submit" value="First slot"/>
    </form>

    <form action="kilkari-testing-tool.jsp">
        <input type="hidden" name="n" value="2"/>
        <input type="submit" value="Second slot"/>
    </form>

    <form action="kilkari-testing-tool.jsp">
        <input type="submit" value="REFRESH"/>
    </form>

    CUSTOM TIME:
    <form action="kilkari-testing-tool.jsp" method="post">
        Day:
        <select name="day">
            <% for (int count = 1; count <= 31; count++) { %>
            <option value="<%=count%>"><%=count%></option>
            <% } %>
        </select>
        Month:
        <select name="month">
            <% for (int count = 1; count <= 12; count++) { %>
            <option value="<%=count%>"><%=count%></option>
            <% } %>
        </select>
        Year:
        <select name="year">
            <% for (int count = 2012; count < 2022; count++) { %>
            <option value="<%=count%>"><%=count%></option>
            <% } %>
        </select>
        <br />
        Hour:
        <select name="hour">
            <% for (int count = 0; count <= 23; count++) { %>
            <option value="<%=count%>"><%=count%></option>
            <% } %>
        </select>
        Min:
        <select name="min">
            <% for (int count = 0; count <= 59; count++) { %>
            <option value="<%=count%>"><%=count%></option>
            <% } %>
        </select>
        Sec:
        <select name="sec">
            <% for (int count = 0; count < 59; count++) { %>
            <option value="<%=count%>"><%=count%></option>
            <% } %>
        </select>
        <input type="submit" />
    </form>
</body>
</html>