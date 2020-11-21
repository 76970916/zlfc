package com.zlfcapp.poster.mvp.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.king.base.util.StringUtils;
import com.king.base.util.ToastUtils;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.bean.FeedBacks;
import com.zlfcapp.poster.bean.Results;
import com.zlfcapp.poster.constantsview.StateButton;
import com.zlfcapp.poster.mvp.adapter.ChatAdapter;
import com.zlfcapp.poster.mvp.adapter.ImageMsgBody;
import com.zlfcapp.poster.mvp.adapter.Message;
import com.zlfcapp.poster.mvp.adapter.MsgSendStatus;
import com.zlfcapp.poster.mvp.adapter.MsgType;
import com.zlfcapp.poster.mvp.adapter.TextMsgBody;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.presenter.FeedBackPresenter;
import com.zlfcapp.poster.mvp.view.IFeedBack;
import com.zlfcapp.poster.utils.ChatUiHelper;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.PictureFileUtil;
import com.zlfcapp.poster.utils.produceReqArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jonathanfinerty.once.Once;

import static jonathanfinerty.once.Once.beenDone;
import static jonathanfinerty.once.Once.markDone;

/**
 * Created by ZhangBx on 2018/4/30.
 */
public class FeedBackFragment extends BaseFragment<IFeedBack, FeedBackPresenter> implements IFeedBack {
    Unbinder unbinder;
    @BindView(R.id.llContent)
    LinearLayout mLlContent;
    @BindView(R.id.rv_chat_list)
    RecyclerView mRvChat;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.bottom_layout)
    RelativeLayout mRlBottomLayout;
    //表情,添加底部布局
    @BindView(R.id.ivAdd)
    ImageView mIvAdd;
    @BindView(R.id.ivEmo)
    ImageView mIvEmo;
    @BindView(R.id.btn_send)
    StateButton mBtnSend;//发送按钮
    @BindView(R.id.iv_lift_return)
    ImageView mIvAudio;//录音图片
    @BindView(R.id.rlEmotion)
    LinearLayout mLlEmotion;//表情布局
    @BindView(R.id.llAdd)
    LinearLayout mLlAdd;//添加布局
    @BindView(R.id.swipe_chat)
    SwipeRefreshLayout mSwipeRefresh;//下拉刷新
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    private static final String SHOW_NEW_SESSION_ROBOT = "reception";

    private ChatAdapter mAdapter;
    public static final String mSenderId = "right";
    public static final String mTargetId = "left";
    public static final int REQUEST_CODE_IMAGE = 0000;
    public static final int REQUEST_CODE_VEDIO = 1111;
    public static final int REQUEST_CODE_FILE = 2222;
    List<Message> mReceiveList = new ArrayList<>();
    List<Message> mSendList = new ArrayList<>();
    private boolean firsthint = true;
    private List<Message> mReceiveMsgList = new ArrayList<Message>();
    int pageCount = 1;
    private ImageView mIvPicture;

    public static FeedBackFragment newInstance() {
        Bundle args = new Bundle();
        FeedBackFragment fragment = new FeedBackFragment();
        fragment.setArguments(args);
        return fragment;
    }


    protected void initContent() {
        ButterKnife.bind(getActivity());
        mAdapter = new ChatAdapter(getActivity(), new ArrayList<Message>());
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(getActivity());
        mRvChat.setLayoutManager(mLinearLayout);
        mRvChat.setAdapter(mAdapter);
        initChatUi();
    }


    private void initChatUi() {
        //mBtnAudio
        final ChatUiHelper mUiHelper = ChatUiHelper.with(getActivity());
        mUiHelper.bindContentLayout(mLlContent)
                .bindttToSendButton(mBtnSend)
                .bindEditText(mEtContent)
                .bindBottomLayout(mRlBottomLayout)
                .bindEmojiLayout(mLlEmotion)
                .bindAddLayout(mLlAdd)
                .bindToAddButton(mIvAdd)
                .bindToEmojiButton(mIvEmo)
                .bindAudioIv(mIvAudio);
        //底部布局弹出,聊天列表上滑
        mRvChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRvChat.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter.getItemCount() > 0) {
                                mRvChat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });
        //点击空白区域关闭键盘
        mRvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                mEtContent.clearFocus();
                mIvEmo.setImageResource(R.mipmap.ic_emoji);
                return false;
            }
        });
        // 单项点击事件
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (mIvPicture == null) {
                    mIvPicture = view.findViewById(R.id.bivPic);
                }
                List<Message> list = mAdapter.getData();
                Message message = list.get(position);
                if (MsgType.IMAGE == message.getMsgType()) {
                    ImageMsgBody body = (ImageMsgBody) message.getBody();
                    // 图片浏览
                    ArrayList<String> paths = new ArrayList<>();
                    paths.add(body.getThumbUrl());
                    CommonUtils.lookImageView(getActivity(), paths, mIvPicture, 1);
                }
            }
        });
    }


    @Override
    public void showProgress() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_feedback;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void initUI() {
        initContent();
        mToolBar.setTitle("意见反馈");
        setSupportActionBarBackgroup(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean isResfresh = false;

    @Override
    public void initData() {
        // 下拉刷新
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isResfresh = true;
                Map<String, String> map = produceReqArg.generateFeedback(CommonUtils.getDevice_id());
                map.put("packageName", context.getPackageName());
                pageCount = pageCount + 1;
                map.put("page", String.valueOf(pageCount));
                map.put("limit", "10");
                map.put("device_id", CommonUtils.getDevice_id());
                // 请求反馈列表
                getPresenter().SubGetFeedBackList(map);
                mSwipeRefresh.setRefreshing(false);
            }
        });
        // 进入页面默认请求第一页
        Map<String, String> map = produceReqArg.generateFeedback(CommonUtils.getDevice_id());
        map.put("packageName", context.getPackageName());
        map.put("page", String.valueOf(pageCount));
        map.put("limit", "10");
        map.put("device_id", CommonUtils.getDevice_id());
        // 请求反馈列表
        getPresenter().SubGetFeedBackList(map);
        mSwipeRefresh.setRefreshing(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.btn_send, R.id.rlPhoto, R.id.iv_lift_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String textMsg = mEtContent.getText().toString().trim();
                if (StringUtils.isEmpty(textMsg)) {
                    ToastUtils.showToast(context, "不能反馈空信息");
                    return;
                }
                sendTextMsg(textMsg);
                mEtContent.setText("");
                if (!beenDone(Once.THIS_APP_INSTALL, SHOW_NEW_SESSION_ROBOT)) {
                    reception();
                }
                Map<String, String> map = produceReqArg.generateFeedback(CommonUtils.getDevice_id());
                map.put("c_number", CommonUtils.getChannel());
                map.put("packageName", context.getPackageName());
                map.put("device_id", CommonUtils.getDevice_id());
                map.put("device", Build.MODEL);
                getPresenter().SubMessage(textMsg, map);
                break;
            case R.id.rlPhoto:
                PictureFileUtil.openGalleryPic(getActivity(), REQUEST_CODE_IMAGE);
                break;
            case R.id.iv_lift_return:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    //文本消息
    private void sendTextMsg(String hello) {
        if (StringUtils.isNotBlank(hello)) {
            final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
            TextMsgBody mTextMsgBody = new TextMsgBody();
            mTextMsgBody.setMessage(hello);
            mMessgae.setBody(mTextMsgBody);
            //开始发送
            mAdapter.addData(mMessgae);
            updateMsg(mMessgae);
        }
    }

    private Message getBaseSendMessage(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }

    private Message getBaseReceiveMessage(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(mTargetId);
        mMessgae.setTargetId(mSenderId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private void updateMsg(final Message mMessgae) {
        mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = 0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    Message mAdapterMessage = mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())) {
                        position = i;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 100);
    }


    private void reception() {
        List<Message> mReceiveMsgList = new ArrayList<>();
        //构建文本消息
        Message mMessgaeText = getBaseReceiveMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage("您好，请问有什么可以帮您? 有什么意见与问题可以告诉我，我会第一时间转达产品小哥哥和小姐姐。");
        mMessgaeText.setBody(mTextMsgBody);
        mReceiveMsgList.add(mMessgaeText);
        mAdapter.addData(mReceiveMsgList.size(), mReceiveMsgList);
        markDone(SHOW_NEW_SESSION_ROBOT);
    }

    @Override
    public FeedBackPresenter createPresenter() {
        return new FeedBackPresenter(getApp());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void OnFeedBackResut(String result) {
        ToastUtils.showToast(getContext(), result);
    }

    @Override
    public void getFeedbackList(FeedBacks feedBacks) {
        //下拉刷新模拟获取历史消息
        //构建文本消息
        List<Results> results = feedBacks.getResults();
        if (results == null || results.size() == 0) {
            pageCount = pageCount - 1;
            return;
        }
        for (int i = results.size() - 1; i >= 0; i--) {
            Results result = results.get(i);
            result.getCreateTime().getTime();
            // 0 -- 用户反馈
            if (result.getType() == 0) {
                // 文字类型
                if (StringUtils.isEmpty(result.getContent())) {
                    break;
                }
                final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(result.getContent());
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                mMessgae.setBody(mTextMsgBody);
                //开始发送
                if (isResfresh) {
                    mAdapter.addData(0, mMessgae);
                } else {
                    mAdapter.addData(mMessgae);
                }
            } else if (result.getType() == 1) {
                // 1 -- 回复
                if (StringUtils.isNotBlank(result.getImg())) {
                    //构建图片消息
                    Message mMessgaeImage = getBaseReceiveMessage(MsgType.IMAGE);
                    ImageMsgBody mImageMsgBody = new ImageMsgBody();
                    mImageMsgBody.setThumbUrl(result.getImg());
                    mMessgaeImage.setBody(mImageMsgBody);
                    mReceiveMsgList.add(mMessgaeImage);
                    if (isResfresh) {
                        mAdapter.addData(0, mMessgaeImage);
                    } else {
                        mAdapter.addData(mMessgaeImage);
                    }
                } else {
                    if (StringUtils.isEmpty(result.getContent())) {
                        break;
                    }
                    Message mMessgaeText = getBaseReceiveMessage(MsgType.TEXT);
                    TextMsgBody mTextMsgBody = new TextMsgBody();
                    mTextMsgBody.setMessage(result.getContent());
                    mMessgaeText.setBody(mTextMsgBody);
                    mMessgaeText.setSentStatus(MsgSendStatus.SENT);
                    mReceiveMsgList.add(mMessgaeText);
                    if (isResfresh) {
                        mAdapter.addData(0, mMessgaeText);
                    } else {
                        mAdapter.addData(mMessgaeText);
                    }
                }

            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
