package com.yswong.discussion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Define Variables

    //This Adapter has different constructors, for different recyclerview

    //0 = RecyclerView of Unit list
    //1 = RecyclerView of Unit thread list
    //2 = RecyclerView of Unit thread content
    //3 = RecyclerView of Forum List
    //3 = RecyclerView of Live chat

    //0
    private String[] unitID;
    private String[] unitName;

    //1
    private ArrayList<Thread> threads = new ArrayList<Thread>();
    private ArrayList<String> replies = new ArrayList<String>();

    //2
    private  ArrayList<String> contents = new ArrayList<>();
    private  ArrayList<String> authors = new ArrayList<>();
    private  ArrayList<String> dates = new ArrayList<>();

    //3
    private  ArrayList<String> title = new ArrayList<>();
    private  ArrayList<String> desc = new ArrayList<>();

    //4
    private ArrayList<LiveThread> liveThreads = new ArrayList<>();

    private Context context;
    private OnNoteListener mOnNoteListener;
    private int views;
    private String uid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();




    //This holder is for the Subject recyclerview
    static class subjectHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView myUnitID, myUnitName;
        OnNoteListener onNoteListener;


        subjectHolder(@NonNull View itemView, OnNoteListener onNoteListener)
        {
            super(itemView);
            myUnitID = itemView.findViewById(R.id.textView_unit_title);
            myUnitName = itemView.findViewById(R.id.textView_unit_name);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteCLick(getAdapterPosition());
        }
    }

    //This holder is for the Thread list recyclerview
    static class viewThreadHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title, author, date, reply;
        OnNoteListener onNoteListener;

        viewThreadHolder(@NonNull View itemView, OnNoteListener onNoteListener)
        {
            super(itemView);

            title = itemView.findViewById(R.id.textView_thread_title);
            author = itemView.findViewById(R.id.textView_thread_author);
            date = itemView.findViewById(R.id.textView_date);
            reply = itemView.findViewById(R.id.textView_replies);

            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteCLick(getAdapterPosition());
        }
    }

    //This holder is for the Thread content recyclerview
    static class ThreadHolder extends  RecyclerView.ViewHolder
    {
        TextView author, date, content, number;
        OnNoteListener onNoteListener;


        ThreadHolder(@NonNull View itemView, OnNoteListener onNoteListener)
        {
            super(itemView);

            author = itemView.findViewById(R.id.textView_this_author);
            content = itemView.findViewById(R.id.textView_this_content);
            date = itemView.findViewById(R.id.textView_this_date);
            number = itemView.findViewById(R.id.textView_this_number);

            this.onNoteListener = onNoteListener;
        }
    }

    //This holder is for the discussion board recyclerview
    static class forumHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView myForumTitle, myForumDesc;
        OnNoteListener onNoteListener;


        forumHolder(@NonNull View itemView, OnNoteListener onNoteListener)
        {
            super(itemView);
            myForumTitle = itemView.findViewById(R.id.textView_forum_title);
            myForumDesc = itemView.findViewById(R.id.textView_forum_desc);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteCLick(getAdapterPosition());
        }
    }

    //This holder is for the live chat recyclerview
    static class liveHolder extends  RecyclerView.ViewHolder
    {
        TextView myLiveAuthor, myLiveDate, myLiveContent;
        ConstraintLayout myLiveBackground;
        CardView myLiveCard;
        OnNoteListener onNoteListener;



        liveHolder(@NonNull View itemView, OnNoteListener onNoteListener)
        {
            super(itemView);



            myLiveCard = itemView.findViewById(R.id.live_message_card);
            myLiveBackground = itemView.findViewById(R.id.livechat_background);
            myLiveAuthor = itemView.findViewById(R.id.textView_live_author);
            myLiveDate = itemView.findViewById(R.id.textView_live_date);
            myLiveContent = itemView.findViewById(R.id.textView_live_content);
            this.onNoteListener = onNoteListener;

        }
    }

    public interface OnNoteListener {
        void onNoteCLick (int position);
    }

    //Constructor that for the unit recycler view
    MyAdapter(Context ct, String[] unitIDs, String[] unitNames, OnNoteListener onNoteListener)
    {
        this.context = ct;
        this.unitID = unitIDs;
        this.unitName = unitNames;
        this.mOnNoteListener = onNoteListener;

        // for the first case, the view type will be 0.
        this.views = 0;
    }

    //Constructor that for viewing thread recycler view
    MyAdapter(Context ct, ArrayList<Thread> threads, ArrayList<String> replies,  OnNoteListener onNoteListener)
    {
        this.context = ct;
        this.threads = threads;
        this.replies = replies;
        this.mOnNoteListener = onNoteListener;

        // for the first case, the view type will be 1.
        this.views = 1;
    }

    //Constructor that for the thread inside recycler view
    MyAdapter(Context ct, ArrayList<String> this_author, ArrayList<String> this_date, ArrayList<String> this_content)
    {
        this.context = ct;
        this.authors = this_author;
        this.dates = this_date;
        this.contents = this_content;

        // for the second case, the view type will be 2.
        this.views = 2;
    }

    //Constructor that for the forum selection recycler view
    MyAdapter(Context ct,  OnNoteListener onNoteListener)
    {
        this.context = ct;

        //As these values are static, so we can manually add them
        this.title.add("Student Discussion");
        this.title.add("Question for Unit Chair");
        this.title.add("Live chat room");

        this.desc.add("General discussion");
        this.desc.add("Ask Unit chair for any issue");
        this.desc.add("Live chat with same unit classmates");

        this.mOnNoteListener = onNoteListener;
        // for the third case, the view type will be 3.
        this.views = 3;
    }

    //Constructor that for the live chat room recycler view
    MyAdapter(Context ct, ArrayList<LiveThread> liveThreads, String uid)
    {
        setHasStableIds(true);
        this.context = ct;
        this.liveThreads = liveThreads;
        this.uid = uid;

        // for the fourth case, the view type will be 4.
        this.views = 4;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return views;
    }



    @NonNull
    @Override
    // To pass the view to the class
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //Depends on the view type, return the corresponding holder
        switch (viewType) {
            case 0:
                return new subjectHolder(inflater.inflate(R.layout.subject_list, parent, false), mOnNoteListener);
            case 1:
                return new viewThreadHolder(inflater.inflate(R.layout.thread_list, parent, false), mOnNoteListener);
            case 2:
                return new ThreadHolder(inflater.inflate(R.layout.reply, parent, false), mOnNoteListener);
            case 3:
                return new forumHolder(inflater.inflate(R.layout.forum_list, parent, false), mOnNoteListener);
            case 4:
                return new liveHolder(inflater.inflate(R.layout.livechat, parent, false), mOnNoteListener);
        }
        return null;

    }


    @Override
    //set the text and images to rows.
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //Depends on the view type, set up the view content
        switch (holder.getItemViewType()) {
            case 0:
                //Unit list recyclerview
                ((subjectHolder) holder).myUnitID.setText(unitID[position]);
                ((subjectHolder) holder).myUnitName.setText(unitName[position]);
                break;
            case 1:
                //Thread list recyclerview
                ((viewThreadHolder) holder).title.setText(threads.get(position).getThreadSubject());
                ((viewThreadHolder) holder).author.setText(threads.get(position).getAuthorName());
                ((viewThreadHolder) holder).date.setText(threads.get(position).getDatetime());
                ((viewThreadHolder) holder).reply.setText("("+replies.get(position)+")");
                break;
            case 2:
                //Thread content recyclerview
                ((ThreadHolder) holder).author.setText(authors.get(position));
                ((ThreadHolder) holder).date.setText(dates.get(position));
                ((ThreadHolder) holder).content.setText(contents.get(position));
                ((ThreadHolder) holder).number.setText("#"+String.valueOf(position));

                //Overrides the MovementMethod of its superclass, allow we retrieve the URL to do justification
                ((ThreadHolder) holder).content.setMovementMethod(new MovementMethod() {
                    @Override
                    public void initialize(TextView widget, Spannable text) {

                    }

                    @Override
                    public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
                        return false;
                    }

                    @Override
                    public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
                        return false;
                    }

                    @Override
                    public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
                        return false;
                    }

                    @Override
                    public void onTakeFocus(TextView widget, Spannable text, int direction) {

                    }

                    @Override
                    public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
                        return false;
                    }

                    @Override
                    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
                        int action = event.getAction();

                        if (action == MotionEvent.ACTION_UP ||
                                action == MotionEvent.ACTION_DOWN) {
                            int x = (int) event.getX();
                            int y = (int) event.getY();

                            x -= widget.getTotalPaddingLeft();
                            y -= widget.getTotalPaddingTop();

                            x += widget.getScrollX();
                            y += widget.getScrollY();

                            Layout layout = widget.getLayout();
                            int line = layout.getLineForVertical(y);
                            int off = layout.getOffsetForHorizontal(line, x);

                            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                            if (link.length != 0) {
                                if (action == MotionEvent.ACTION_UP) {

                                    Intent intent;
                                    if(buffer.toString().contains("youtube.com/watch") || buffer.toString().contains("youtu.be/"))
                                    {
                                        intent = new Intent(context, YoutubeActivity.class);
                                        intent.putExtra("videoId", extractYTId(buffer.toString()));
                                    }
                                    else
                                    {
                                        intent = new Intent(context, WebViewActivity.class);
                                        intent.putExtra("URL", extractUrl(buffer.toString()));
                                    }

                                    context.startActivity(intent);
                                    Selection.removeSelection(buffer);


                                } else if (action == MotionEvent.ACTION_DOWN) {
                                    Selection.setSelection(buffer,
                                            buffer.getSpanStart(link[0]),
                                            buffer.getSpanEnd(link[0]));
                                }

                                return true;
                            } else {
                                Selection.removeSelection(buffer);
                            }
                        }

                        return false;
                    }

                    @Override
                    public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
                        return false;
                    }

                    @Override
                    public boolean canSelectArbitrarily() {
                        return false;
                    }
                });
                break;
            case 3:
                //Select discussion board recyclerview
                ((forumHolder) holder).myForumTitle.setText(title.get(position));
                ((forumHolder) holder).myForumDesc.setText(desc.get(position));
                break;
            case 4:
                //Live chat recyclerview
                holder.setIsRecyclable(false);
                RelativeLayout.LayoutParams params;
                params = (RelativeLayout.LayoutParams) ((liveHolder) holder).myLiveCard.getLayoutParams();
                ((liveHolder) holder).myLiveAuthor.setText(liveThreads.get(position).getAuthorName());
                ((liveHolder) holder).myLiveDate.setText(liveThreads.get(position).getDatetime());
                ((liveHolder) holder).myLiveContent.setText(liveThreads.get(position).getThreadContent());

                // This is use to determine if the message is sent by the current user
                // If so, adjust the chat bubble to right hand side and change the background colour
                if(liveThreads.get(position).getAuthorId().equals(uid)){
                    //align the card view to right
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((liveHolder) holder).myLiveCard.setLayoutParams(params);
                }
                else
                {
                    //align the card view to left
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((liveHolder) holder).myLiveCard.setLayoutParams(params);
                    ((liveHolder) holder).myLiveBackground.setBackgroundColor(Color.WHITE);
                    ((liveHolder) holder).myLiveAuthor.setTextColor(Color.BLACK);
                    ((liveHolder) holder).myLiveDate.setTextColor(Color.parseColor("#5E5E5E"));
                    ((liveHolder) holder).myLiveContent.setTextColor(Color.BLACK);
                }
                break;
        }

    }

    //This method is for extract Youtube Video Id from Youtube links.
    public static String extractYTId(String youtubeUrl) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl);

        if(matcher.find()){
            return matcher.group();
        }
        return "";
    }

    //This method is for extract URL link from string.
    public static String extractUrl(String Url) {
        String pattern = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(Url);

        if(matcher.find()){
            return matcher.group();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        switch (views){
            case 0:
                return unitID.length;
            case 1:
                return threads.size();
            case 2:
                return authors.size();
            case 3:
                return 3;
            case 4:
                return liveThreads.size();
        }
        return -1;
    }
}
