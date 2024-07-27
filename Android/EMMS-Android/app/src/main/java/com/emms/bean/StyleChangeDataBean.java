package com.emms.bean;

import java.util.List;

public class StyleChangeDataBean {


    /**
     * code : 200
     * message : null
     * data : [{"id":"5d0add1e12b7ee000130b811","createdTime":null,"modifiedTime":null,"factory":"GLG","ordertask":{"orderno":"18R18041GB02","sewingline":"WS07","starttime":"2019-06-20 06:00:00"},"orderaccept":null,"handlerecord":null,"description":"测试描述","status":1,"createusername":"系统员","createuserno":"00001","createtime":"2019-06-20 01:10:54","updatetime":"2019-06-20 01:10:54","setField1":{"Value":"","Key":"Part"},"setField2":{"Value":"","Key":"SubPart"},"setField3":{"Value":"18R18041GB02/WS07/2019-06-20 06:00:00","Key":"RequestDate"},"setField4":{"Value":"","Key":""},"setField5":{"Value":"","Key":""}},{"id":"5d27e80fa8bea500015e2acc","createdTime":"2019-07-12 01:53:19.382","modifiedTime":"2019-07-12 01:53:19.382","factory":"GLG","ordertask":{"orderno":"19R06179US02","sewingline":"2127","starttime":"2019-07-11 04:55:59"},"orderaccept":null,"handlerecord":null,"description":"1562820959532","status":1,"createusername":"周波","createuserno":"test-transfer-task-api","createtime":"2019-07-12 01:53:19","updatetime":null,"setField1":{"Value":"","Key":"Part"},"setField2":{"Value":"","Key":"SubPart"},"setField3":{"Value":"19R06179US02/2127/2019-07-11 04:55:59","Key":"RequestDate"},"setField4":{"Value":"","Key":""},"setField5":{"Value":"","Key":""}},{"id":"5d27e94cfd975c00017464da","createdTime":"2019-07-12 01:58:36.455","modifiedTime":"2019-07-12 01:58:36.455","factory":"GLG","ordertask":{"orderno":"19R06179CA06","sewingline":"2127","starttime":"2019-07-12 09:58:00"},"orderaccept":null,"handlerecord":null,"description":"","status":1,"createusername":"周波","createuserno":"20212","createtime":"2019-07-12 01:58:36","updatetime":null,"setField1":{"Value":"","Key":"Part"},"setField2":{"Value":"","Key":"SubPart"},"setField3":{"Value":"19R06179CA06/2127/2019-07-12 09:58:00","Key":"RequestDate"},"setField4":{"Value":"","Key":""},"setField5":{"Value":"","Key":""}}]
     * total : 3
     * page : 1
     * size : 10
     */

    private int code;
    private Object message;
    private int total;
    private int page;
    private int size;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 5d0add1e12b7ee000130b811
         * createdTime : null
         * modifiedTime : null
<<<<<<< Updated upstream
         * factory : GLGs
=======
         * factory : GLG
>>>>>>> Stashed changes
         * ordertask : {"orderno":"18R18041GB02","sewingline":"WS07","starttime":"2019-06-20 06:00:00"}
         * orderaccept : null
         * handlerecord : null
         * description : 测试描述
         * status : 1
         * createusername : 系统员
         * createuserno : 00001
         * createtime : 2019-06-20 01:10:54
         * updatetime : 2019-06-20 01:10:54
         * setField1 : {"Value":"","Key":"Part"}
         * setField2 : {"Value":"","Key":"SubPart"}
         * setField3 : {"Value":"18R18041GB02/WS07/2019-06-20 06:00:00","Key":"RequestDate"}
         * setField4 : {"Value":"","Key":""}
         * setField5 : {"Value":"","Key":""}
         */

        private String id;
        private Object createdTime;
        private Object modifiedTime;
        private String factory;
        private String receiver;//kingzhang
        private OrdertaskBean ordertask;
        private Object orderaccept;
        private Object handlerecord;
        private String description;
        private int status;
        private String createusername;
        private String createuserno;
        private String createtime;
        private String updatetime;
        private SetField1Bean setField1;
        private SetField2Bean setField2;
        private SetField3Bean setField3;
        private SetField4Bean setField4;
        private SetField5Bean setField5;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(Object createdTime) {
            this.createdTime = createdTime;
        }

        public Object getModifiedTime() {
            return modifiedTime;
        }

        public void setModifiedTime(Object modifiedTime) {
            this.modifiedTime = modifiedTime;
        }

        public String getFactory() {
            return factory;
        }
         public String getReceiver() {
            return receiver;
        }//kingzhang

        public void setFactory(String factory) {
            this.factory = factory;
        }

        public OrdertaskBean getOrdertask() {
            return ordertask;
        }

        public void setOrdertask(OrdertaskBean ordertask) {
            this.ordertask = ordertask;
        }

        public Object getOrderaccept() {
            return orderaccept;
        }

        public void setOrderaccept(Object orderaccept) {
            this.orderaccept = orderaccept;
        }

        public Object getHandlerecord() {
            return handlerecord;
        }

        public void setHandlerecord(Object handlerecord) {
            this.handlerecord = handlerecord;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreateusername() {
            return createusername;
        }

        public void setCreateusername(String createusername) {
            this.createusername = createusername;
        }

        public String getCreateuserno() {
            return createuserno;
        }

        public void setCreateuserno(String createuserno) {
            this.createuserno = createuserno;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public SetField1Bean getSetField1() {
            return setField1;
        }

        public void setSetField1(SetField1Bean setField1) {
            this.setField1 = setField1;
        }

        public SetField2Bean getSetField2() {
            return setField2;
        }

        public void setSetField2(SetField2Bean setField2) {
            this.setField2 = setField2;
        }

        public SetField3Bean getSetField3() {
            return setField3;
        }

        public void setSetField3(SetField3Bean setField3) {
            this.setField3 = setField3;
        }

        public SetField4Bean getSetField4() {
            return setField4;
        }

        public void setSetField4(SetField4Bean setField4) {
            this.setField4 = setField4;
        }

        public SetField5Bean getSetField5() {
            return setField5;
        }

        public void setSetField5(SetField5Bean setField5) {
            this.setField5 = setField5;
        }

        public static class OrdertaskBean {
            /**
             * orderno : 18R18041GB02
             * sewingline : WS07
             * starttime : 2019-06-20 06:00:00
             */

            private String orderno;
            private String sewingline;
            private String starttime;

            public String getOrderno() {
                return orderno;
            }

            public void setOrderno(String orderno) {
                this.orderno = orderno;
            }

            public String getSewingline() {
                return sewingline;
            }

            public void setSewingline(String sewingline) {
                this.sewingline = sewingline;
            }

            public String getStarttime() {
                return starttime;
            }

            public void setStarttime(String starttime) {
                this.starttime = starttime;
            }
        }

        public static class SetField1Bean {
            /**
             * Value :
             * Key : Part
             */

            private String Value;
            private String Key;

            public String getValue() {
                return Value;
            }

            public void setValue(String Value) {
                this.Value = Value;
            }

            public String getKey() {
                return Key;
            }

            public void setKey(String Key) {
                this.Key = Key;
            }
        }

        public static class SetField2Bean {
            /**
             * Value :
             * Key : SubPart
             */

            private String Value;
            private String Key;

            public String getValue() {
                return Value;
            }

            public void setValue(String Value) {
                this.Value = Value;
            }

            public String getKey() {
                return Key;
            }

            public void setKey(String Key) {
                this.Key = Key;
            }
        }

        public static class SetField3Bean {
            /**
             * Value : 18R18041GB02/WS07/2019-06-20 06:00:00
             * Key : RequestDate
             */

            private String Value;
            private String Key;

            public String getValue() {
                return Value;
            }

            public void setValue(String Value) {
                this.Value = Value;
            }

            public String getKey() {
                return Key;
            }

            public void setKey(String Key) {
                this.Key = Key;
            }
        }

        public static class SetField4Bean {
            /**
             * Value :
             * Key :
             */

            private String Value;
            private String Key;

            public String getValue() {
                return Value;
            }

            public void setValue(String Value) {
                this.Value = Value;
            }

            public String getKey() {
                return Key;
            }

            public void setKey(String Key) {
                this.Key = Key;
            }
        }

        public static class SetField5Bean {
            /**
             * Value :
             * Key :
             */

            private String Value;
            private String Key;

            public String getValue() {
                return Value;
            }

            public void setValue(String Value) {
                this.Value = Value;
            }

            public String getKey() {
                return Key;
            }

            public void setKey(String Key) {
                this.Key = Key;
            }
        }
    }
}
