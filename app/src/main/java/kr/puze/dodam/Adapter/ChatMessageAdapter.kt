package kr.puze.dodam.Adapter

import android.content.Context
import android.view.ViewGroup
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.LayoutInflater
import kr.puze.dodam.R
import kr.puze.dodam.Data.ChatMessage
import kr.puze.dodam.Utils.Constants
import butterknife.BindView
import kr.puze.dodam.Utils.BaseViewHolder

class ChatMessageAdapter : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    private val SYSTEM_VIEW = 0
    private val SELF_VIEW = 1
    private val RECEIVED_VIEW = 2

    private var mContext: Context? = null
    private var mData: ArrayList<ChatMessage>? = null

    fun ChatMessageAdapter(mContext: Context) {
        this.mContext = mContext
    }

    fun addItems(item: ChatMessage) {
        if (mData == null) {
            mData = ArrayList()
        }
        mData!!.add(item)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val (_, messageType) = mData!!.get(position)
        val constants = Constants()

        return when (messageType) {
            constants.MESSAGE_TYPE_SYSTEM -> SYSTEM_VIEW
            constants.MESSAGE_TYPE_SELF -> SELF_VIEW
            constants.MESSAGE_TYPE_RECEIVE -> RECEIVED_VIEW
            else -> super.getItemViewType(position)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var view: View? = null

        return when (viewType) {
            SYSTEM_VIEW -> {
                view = inflater.inflate(R.layout.item_system_message, parent, false)
                SystemHolder(view!!)
            }
            SELF_VIEW -> {
                view = inflater.inflate(R.layout.item_self_message, parent, false)
                SelfHolder(view!!)
            }
            RECEIVED_VIEW -> {
                view = inflater.inflate(R.layout.item_received_message, parent, false)
                ReceivedHolder(view!!)
            }
            else -> {
                ViewHolder(view!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val (userAction, _, messageOwner, messageContent) = mData!!.get(position)

        if (viewType == SYSTEM_VIEW) {
            val systemHolder = holder as SystemHolder

            val messageSuffix = if (userAction == "entered") "님이 채팅방에 들어왔습니다." else "님이 채팅방을 나갔습니다."
            systemHolder.messageTextView!!.text = messageOwner + messageSuffix
        } else if (viewType == SELF_VIEW) {
            val selfHolder = holder as SelfHolder

            selfHolder.messageTextView!!.text = messageContent
        } else if (viewType == RECEIVED_VIEW) {
            val receivedHolder = holder as ReceivedHolder

            receivedHolder.messageOwnerTextView!!.text = messageOwner
            receivedHolder.messageTextView!!.text = messageContent
        }
    }

    open class ViewHolder(itemView: View) : BaseViewHolder(itemView)

    class SystemHolder(itemView: View) : ViewHolder(itemView) {
        @BindView(R.id.messageTextView)
        internal var messageTextView: AppCompatTextView? = null
    }


    class SelfHolder(itemView: View) : ViewHolder(itemView) {
        @BindView(R.id.messageTextView)
        internal var messageTextView: AppCompatTextView? = null
    }

    class ReceivedHolder(itemView: View) : ViewHolder(itemView) {
        @BindView(R.id.messageTextView)
        internal var messageTextView: AppCompatTextView? = null
        @BindView(R.id.messageOwnerTextView)
        internal var messageOwnerTextView: AppCompatTextView? = null
    }

}