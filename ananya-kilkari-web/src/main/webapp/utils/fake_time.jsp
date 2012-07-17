<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>
<%
    try {
        if (request.getMethod().equals("POST")) {
            String offsetValue = System.getProperty("faketime.offset.seconds");
            long currentOffset = Long.parseLong(offsetValue == null ? "0" : offsetValue);

            String dateTime = request.getParameter("newDateTime");

            Date newDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateTime);
            System.out.println("Current Time:" + new Date());
            System.out.println("Request for Updated Time:" + newDateTime);

            long newOffset = ((newDateTime.getTime() - System.currentTimeMillis()) / 1000) + currentOffset;
            System.setProperty("faketime.offset.seconds", String.valueOf(newOffset));

            System.out.println("Updated Time:" + new Date());


        }
    } catch (java.lang.Exception e) {
        out.println("Error: " + e.getMessage());
        return;
    }

%>
<html>

<head>
    <link rel="stylesheet" media="all" type="text/css" href="../css/utils/jquery-ui-theme.css"/>
    <style type="text/css">
        .ui-timepicker-div .ui-widget-header {
            margin-bottom: 8px;
        }

        .ui-timepicker-div dl {
            text-align: left;
        }

        .ui-timepicker-div dl dt {
            height: 25px;
            margin-bottom: -25px;
        }

        .ui-timepicker-div dl dd {
            margin: 0 10px 10px 65px;
        }

        .ui-timepicker-div td {
            font-size: 90%;
        }

        .ui-tpicker-grid-label {
            background: none;
            border: none;
            margin: 0;
            padding: 0;
        }
    </style>

    <script type="text/javascript" src="../js/jquery/jquery.js"></script>
    <script type="text/javascript" src="../js/utils/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="../js/utils/jquery-datetimepicker-addon.js"></script>
    <script type="text/javascript" src="../js/utils/jquery-ui-slider.js"></script>

    <script type="text/javascript">
        $(function () {
            $('#newDateTime').datetimepicker({
                dateFormat:'dd/mm/yy',
                timeFormat:'hh:mm'
            });
        });
    </script>

</head>

<body>
Current Time : <%= new Date()%>
<div>

    <form action="fake_time.jsp" method="post">
        <label for="newDateTime">New Date Time</label>
        <input type="text" name="newDateTime" id="newDateTime" value=""/>
        <input type="submit"/>
    </form>
</div>
</body>
</html>