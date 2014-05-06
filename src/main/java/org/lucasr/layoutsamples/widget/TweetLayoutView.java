/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lucasr.layoutsamples.widget;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.lucasr.layoutsamples.adapter.Tweet;
import org.lucasr.layoutsamples.adapter.TweetPresenter;
import org.lucasr.layoutsamples.app.R;
import org.lucasr.layoutsamples.util.ImageUtils;

import java.util.EnumMap;
import java.util.EnumSet;

public class TweetLayoutView extends ViewGroup implements TweetPresenter {
    private final ImageView mProfileImage;
    private final TextView mAuthorText;
    private final TextView mMessageText;
    private final ImageView mPostImage;
    private final EnumMap<Action, ImageView> mActionIcons;

    public TweetLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TweetLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.tweet_layout_view, this, true);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mAuthorText = (TextView) findViewById(R.id.author_text);
        mMessageText = (TextView) findViewById(R.id.message_text);
        mPostImage = (ImageView) findViewById(R.id.post_image);

        mActionIcons = new EnumMap(Action.class);
        for (Action action : Action.values()) {
            final ImageView icon;
            switch (action) {
                case REPLY:
                    icon = (ImageView) findViewById(R.id.reply_action);
                    break;

                case RETWEET:
                    icon = (ImageView) findViewById(R.id.retweet_action);
                    break;

                case FAVOURITE:
                    icon = (ImageView) findViewById(R.id.favourite_action);
                    break;

                default:
                    throw new IllegalArgumentException("Unrecognized tweet action");
            }

            mActionIcons.put(action, icon);
        }
    }

    private void layoutView(View view, int left, int top, int width, int height) {
        MarginLayoutParams margins = (MarginLayoutParams) view.getLayoutParams();
        final int leftWithMargins = left + margins.leftMargin;
        final int topWithMargins = top + margins.topMargin;

        view.layout(leftWithMargins, topWithMargins,
                    leftWithMargins + width, topWithMargins + height);
    }

    private int getWidthWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getWidth() + lp.leftMargin + lp.rightMargin;
    }

    private int getHeightWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }

    private int getMeasuredWidthWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
    }

    private int getMeasuredHeightWithMargins(View child) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int widthUsed = getPaddingLeft() + getPaddingRight();
        int heightUsed = getPaddingTop() + getPaddingBottom();

        measureChildWithMargins(mProfileImage,
                                widthMeasureSpec, widthUsed,
                                heightMeasureSpec, heightUsed);
        widthUsed += getMeasuredHeightWithMargins(mProfileImage);

        measureChildWithMargins(mAuthorText,
                                widthMeasureSpec, widthUsed,
                                heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(mAuthorText);

        measureChildWithMargins(mMessageText,
                                widthMeasureSpec, widthUsed,
                                heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(mMessageText);

        if (mPostImage.getVisibility() != View.GONE) {
            measureChildWithMargins(mPostImage,
                                    widthMeasureSpec, widthUsed,
                                    heightMeasureSpec, heightUsed);
            heightUsed += getMeasuredHeightWithMargins(mPostImage);
        }

        int maxIconHeight = 0;
        for (Action action : Action.values()) {
            final View iconView = mActionIcons.get(action);
            measureChildWithMargins(iconView,
                                    widthMeasureSpec, widthUsed,
                                    heightMeasureSpec, heightUsed);

            final int height = getMeasuredHeightWithMargins(iconView);
            if (height > maxIconHeight) {
                maxIconHeight = height;
            }

            widthUsed += getMeasuredHeightWithMargins(iconView);
        }
        heightUsed += maxIconHeight;

        final int width = widthSize + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(width, heightUsed);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        int top = paddingTop;

        layoutView(mProfileImage, paddingLeft, top,
                   mProfileImage.getMeasuredWidth(),
                   mProfileImage.getMeasuredHeight());

        final int contentLeft = getWidthWithMargins(mProfileImage) + paddingLeft;

        layoutView(mAuthorText, contentLeft, top,
                   mAuthorText.getMeasuredWidth(),
                   mAuthorText.getMeasuredHeight());
        top += getHeightWithMargins(mAuthorText);

        layoutView(mMessageText, contentLeft, top,
                   mMessageText.getMeasuredWidth(),
                   mMessageText.getMeasuredHeight());
        top += getHeightWithMargins(mMessageText);

        if (mPostImage.getVisibility() != View.GONE) {
            layoutView(mPostImage, contentLeft, top,
                       mPostImage.getMeasuredWidth(),
                       mPostImage.getMeasuredHeight());

            top += getHeightWithMargins(mPostImage);
        }

        int iconsLeft = contentLeft;
        for (Action action : Action.values()) {
            final ImageView icon = mActionIcons.get(action);

            layoutView(icon, iconsLeft, top,
                       icon.getMeasuredWidth(),
                       icon.getMeasuredHeight());
            iconsLeft += getWidthWithMargins(icon);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void update(Tweet tweet, EnumSet<UpdateFlags> flags) {
        mAuthorText.setText(tweet.getAuthorName());
        mMessageText.setText(tweet.getMessage());

        final Context context = getContext();
        ImageUtils.loadImage(context, mProfileImage, tweet.getProfileImageUrl(), flags);

        final boolean hasPostImage = !TextUtils.isEmpty(tweet.getPostImageUrl());
        mPostImage.setVisibility(hasPostImage ? View.VISIBLE : View.GONE);
        if (hasPostImage) {
            ImageUtils.loadImage(context, mPostImage, tweet.getPostImageUrl(), flags);
        }
    }
}
