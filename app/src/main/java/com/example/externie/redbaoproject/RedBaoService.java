package com.example.externie.redbaoproject;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by externIE on 2017/1/22.
 */
public class RedBaoService extends AccessibilityService {

    private static final String RedBaoPackage    = "com.tencent.mm:id/a48";//红包的资源ID
    private static final String BtnKai           = "com.tencent.mm:id/be_";//按钮“开”的资源ID
    private static final String BtnBack          = "com.tencent.mm:id/gr"; //回退键的资源ID

    //该对象代表了整个窗口视图的快照
    private AccessibilityNodeInfo mRootNodeInfo = null;
    //已经拆过的红包收集容器
    private Set<AccessibilityNodeInfo> RB_OPENED_Set = new HashSet<AccessibilityNodeInfo>();
    //微信红包收集器
    private Set<AccessibilityNodeInfo> MC_RB_Set = new HashSet<AccessibilityNodeInfo>();

    private Set<AccessibilityNodeInfo> RootRes_Set = new HashSet<AccessibilityNodeInfo>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        mRootNodeInfo = accessibilityEvent.getSource();

        RootRes_Set.add(mRootNodeInfo);
        int size = RootRes_Set.size();


        if(mRootNodeInfo == null)
            return;//没有获得窗口视图快照

        if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            //窗口内容发生了变化
            this.onContentChange();
        }

        if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            //窗口状态发生了变化
            this.onWindowStateChange();
        }
    }

    @Override
    public void onInterrupt() {

    }

    /*
    * 窗口内容发生变化的时候*/
    private void onContentChange(){
        checkRedBaoAndClickIt(null);
        checkBtnBackAndClickIt();
    }

    /*
    * 窗口状态发生变化*/
    private void onWindowStateChange(){
        //checkRedBaoAndClickIt(null);
        checkKaiAndClickIt();
        checkBtnBackAndClickIt();
    }


    /*
    * 检查并拆掉没有拆过的红包
    *String strRedBaoSearchKeyWord 是搜索当前窗口中红包的关键字，微信红包的关键字为“领取红包”
    * 当参数为null的时候，采用resource-id寻找的方法*/
    private void checkRedBaoAndClickIt(String strRedBaoSearchKeyWord){
        List<AccessibilityNodeInfo> nodeList;
        if (strRedBaoSearchKeyWord != null){
            nodeList = getNodeListByText(strRedBaoSearchKeyWord);
        }else{
            nodeList =  getNodeListByResID(RedBaoPackage);
        }
        if (nodeList == null) return;

        if(nodeList.size()>0) {
            //至少找到一个红包的NodeInfo

            /*
            *想法一：找到用一个Set集合来存取打开过的NodeInfo，但我发现一个悲剧的事实，就是每次新事件到来
            * 所带来的NodeList里面的Node对象和存在Set里面的是一摸一样的，也就是说即便是新的红包出现
            * 只要当前界面的红包节点数小于set里面的红包节点数，set就会拒绝相同的节点存入啦，所以新的红包就被认为是拆过的
            * 很悲剧！！！所以想法一 Failed！！！
            */

            /*Iterator<AccessibilityNodeInfo> it = nodeList.iterator();
            while (it.hasNext()) {
                AccessibilityNodeInfo tempNode = it.next();
                if (RB_OPENED_Set.add(tempNode)) {//存在一个没有打开过的红包，那么我们现在贱贱的打开它吧，哈哈哈。
                    System.out.println("找到一个没有打开过的红包");
                    showTip("找到一个没有打开过的红包");
                    //点击该节点
                    tempNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                } else {
                    System.out.println("找到一个已经拆过的红包");
                    showTip("找到一个已经拆过的红包");
                }
            }*/

            /*
            * 想法二：不管三七二十一，把当前界面最下面的一个红包节点打开
            * 这样做的话，在windowState事件里面就不能有判断红包节点的动作，要不然就会无限循环啦
            * 可能有办法解决这样的无限循环
            * 于是我把windowState事件里面的红包检查给去掉了
            * 只剩下contentChange事件里面的红包检查
            * 这同样带来一个问题就是，你不能滑动你的屏幕啦，滑动就会默认去点击最后一个红包，即便它被拆过
            * 还有，当你第一次进入群的时候，service是不会去点击没有抢过的红包的
            * 只有有人新发了红包service才能正常工作
            * 也就是说在想法二下你需要这么做
            * 1。保持屏幕常亮
            * 2。不动手机
            * 3。默默等红包
            * 虽然很傻，但至少它开始工作了。。。
            * */
            AccessibilityNodeInfo lastOne = nodeList.get(nodeList.size() - 1);
            lastOne.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            System.out.println("找到最新的一个，并拆开");
            showTip("找到最新的一个，并拆开");
        }
    }

    /*
    * 寻找“开”字按钮并点击它*/
    private void checkKaiAndClickIt(){
        boolean bSuccess = findViewByIDAndClickIt(BtnKai);
        if(bSuccess){
            System.out.println("点击开");
            showTip("点击开");
        }else{
            System.out.println("没找到开");
            showTip("没找到开");
        }
    }

    /*
    * 寻找“返回”按钮并点击它*/
    private void checkBtnBackAndClickIt(){
        boolean bSuccess = findViewByIDAndClickIt(BtnBack);
        if(bSuccess){
            System.out.println("点击返回");
            showTip("点击返回");
        }else{
            System.out.println("没找到返回");
            showTip("没找到返回");
        }
    }

    /*
    * 找到对应的res-id的控件并点击*/
    private boolean findViewByIDAndClickIt(String strID){
        List<AccessibilityNodeInfo> nodeList = getNodeListByResID(strID);
        if (nodeList == null) return false;
        if(nodeList.size()>0) {
            AccessibilityNodeInfo lastOne = nodeList.get(nodeList.size() - 1);
            lastOne.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        }
        return false;
    }

    /*
    * 根据资源ID来获取节点列表*/
    private List<AccessibilityNodeInfo> getNodeListByResID(String resID){
        if (mRootNodeInfo != null){
            //注意：findAccessibilityNodeInfosByViewId API18以上才可以用哦
            return mRootNodeInfo.findAccessibilityNodeInfosByViewId(resID);
        }else{
            return null;
        }
    }

    /*
    * 根据关键词来获取节点列表*/
    private List<AccessibilityNodeInfo> getNodeListByText(String strKeyWord){
        return getNodeListByResID(strKeyWord);
    }

    private void showTip(String strTip){
        Toast.makeText(this, strTip, Toast.LENGTH_SHORT).show();
        System.out.print("externIE: "+strTip);
    }
}
