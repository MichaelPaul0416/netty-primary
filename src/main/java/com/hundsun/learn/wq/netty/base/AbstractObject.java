package com.hundsun.learn.wq.netty.base;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/6
 */
public abstract class AbstractObject {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstractObject.class);

    protected static int checkPort(String args[],int defaultPort){
        if(args != null && args.length > 0 ){
            try {
                return Integer.parseInt(args[0]);
            }catch (NumberFormatException e){
                logger.error("转换入参端口["+args[0]+"]为整型失败，请检查后重新输入端口号...");
                e.printStackTrace();
            }
        }
        return  defaultPort;
    }
}
