package com.dozuki.ifixit.guide_create.ui;

import java.util.ArrayList;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.EditText;

import com.dozuki.ifixit.MainApplication;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.gallery.model.MediaInfo;
import com.dozuki.ifixit.gallery.ui.GalleryActivity;
import com.dozuki.ifixit.guide_create.ui.GuideCreateStepEditFragmentNew.GuideStepChangedListener;
import com.dozuki.ifixit.guide_view.model.StepImage;
import com.ifixit.android.imagemanager.ImageManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GuideCreateEditMediaFragment extends Fragment implements OnClickListener, OnLongClickListener {

   private static String NO_TITLE = "Title";
   public static int NO_IMAGE = -1;;
   public static final int IMAGE_KEY_1 = 1;
   public static final int IMAGE_KEY_2 = 2;
   public static final int IMAGE_KEY_3 = 3;
   private static final String TITLE_KEY = "TITLE_KEY";
   private static final int REPLACE_IMAGE_ID = 1;
   private static final int REMOVE_IMAGE_ID = 2;

   EditText mStepTitle;
   // images
   ImageManager mImageManager;
   ImageView mLargeImage;
   ImageView mImageOne;
   ImageView mImageTwo;
   ImageView mImageThree;
   StepImage mImageOneInfo = new StepImage(NO_IMAGE);
   StepImage mImageTwoInfo = new StepImage(NO_IMAGE);
   StepImage mImageThreeInfo = new StepImage(NO_IMAGE);
   private int mCurSelectedKey;

   // video

   // title
   String mTitle = NO_TITLE;
   private QuickAction mQuickAction;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mImageManager = ((MainApplication) getActivity().getApplication()).getImageManager();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View v = inflater.inflate(R.layout.guide_create_edit_media, container, false);
      mStepTitle = (EditText) v.findViewById(R.id.step_edit_title_text);
      mLargeImage = (ImageView) v.findViewById(R.id.step_edit_large_image);
      mImageOne = (ImageView) v.findViewById(R.id.step_edit_thumb_1);
      mImageOne.setOnLongClickListener(this);
      mImageOne.setOnClickListener(this);
      mImageTwo = (ImageView) v.findViewById(R.id.step_edit_thumb_2);
      mImageTwo.setOnLongClickListener(this);
      mImageTwo.setOnClickListener(this);
      mImageThree = (ImageView) v.findViewById(R.id.step_edit_thumb_3);
      mImageThree.setOnLongClickListener(this);
      mImageThree.setOnClickListener(this);

      if (savedInstanceState != null) {

         mImageOneInfo = (StepImage) savedInstanceState.getSerializable("" + IMAGE_KEY_1);

         mImageTwoInfo = (StepImage) savedInstanceState.getSerializable("" + IMAGE_KEY_2);

         mImageThreeInfo = (StepImage) savedInstanceState.getSerializable("" + IMAGE_KEY_3);

         mTitle = savedInstanceState.getString(TITLE_KEY);

      }
      
      ActionItem addAction =
         new ActionItem(REPLACE_IMAGE_ID, "Replace Image", getResources().getDrawable(R.drawable.ic_menu_bot_step_add));
      ActionItem delAction =
         new ActionItem(REMOVE_IMAGE_ID, "Remove Image", getResources().getDrawable(R.drawable.ic_menu_bot_step_delete));

      mQuickAction = new QuickAction(getActivity());
      mQuickAction.addActionItem(addAction);
      mQuickAction.addActionItem(delAction);
      mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
         

         @Override
         public void onItemClick(QuickAction source, int pos, int actionId) {
            switch(actionId)
            {
               case REPLACE_IMAGE_ID:
                  Intent intent = new Intent(getActivity(), GalleryActivity.class);
                  intent.putExtra(GalleryActivity.ACTIVITY_RETURN_MODE, 1);
                  startActivityForResult(intent, mCurSelectedKey);
                  break;
               case REMOVE_IMAGE_ID:
                  if(mCurSelectedKey == IMAGE_KEY_1) {
                     mImageOneInfo = new StepImage(NO_IMAGE);
                     setImage(IMAGE_KEY_1);
                  }
                  if(mCurSelectedKey == IMAGE_KEY_2) {
                     mImageTwoInfo = new StepImage(NO_IMAGE);
                     setImage(IMAGE_KEY_2);
                  }
                  if(mCurSelectedKey == IMAGE_KEY_3) {
                     mImageThreeInfo = new StepImage(NO_IMAGE);
                     setImage(IMAGE_KEY_3);
                  }
                     
                  break;
            }
            
         }
      });

      mStepTitle.setText(mTitle);
      mStepTitle.addTextChangedListener(new TextWatcher() {

         @Override
         public void afterTextChanged(Editable s) {
            Log.i("GuideCreateStepEditFragment", "GuideTitle changed to: " + s.toString());
            mTitle = s.toString();
            setGuideDirty();
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mStepTitle.selectAll();
         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {}

      });

      fitImagesToSpace(v.getLayoutParams().height, v.getLayoutParams().width);
      setImage(IMAGE_KEY_1);
      setImage(IMAGE_KEY_2);
      setImage(IMAGE_KEY_3);

      return v;
   }

   public void fitImagesToSpace(float vHeight, float vWidth) {
      FragmentActivity context = getActivity();
      Resources resources = context.getResources();
      DisplayMetrics metrics = new DisplayMetrics();
      context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

      float screenHeight = metrics.heightPixels;
      float screenWidth = metrics.widthPixels;
      float thumbnailHeight = 0f;
      float thumbnailWidth = 0f;
      float height = 0f;
      float width = 0f;
      float titleHeight = mStepTitle.getHeight();

      float thumbPadding = resources.getDimensionPixelSize(R.dimen.guide_thumbnail_padding) * 2f;
      float mainPadding = resources.getDimensionPixelSize(R.dimen.guide_image_padding) * 2f;
      float pagePadding = resources.getDimensionPixelSize(R.dimen.page_padding) * 2f;

      float padding = pagePadding + mainPadding + thumbPadding;

      // Portrait orientation
      if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
         padding += resources.getDimensionPixelSize(R.dimen.guide_image_spacing_right);

         width = (((screenWidth - padding - titleHeight) / 8f) * 5f);
         height = width * (3f / 4f);

         thumbnailHeight = (1f / 3f) * height;
         thumbnailWidth = (thumbnailHeight * (4f / 3f));

      } else {
         int actionBarHeight =
            resources.getDimensionPixelSize(com.actionbarsherlock.R.dimen.abs__action_bar_default_height);

         int indicatorHeight = ((GuideCreateStepsEditActivity) context).getIndicatorHeight();

         if (indicatorHeight == 0) {
            indicatorHeight = 49;
         }

         padding += resources.getDimensionPixelSize(R.dimen.guide_image_spacing_bottom);

         padding += resources.getDimensionPixelSize(R.dimen.guide_image_spacing_bottom);

         height = (((screenHeight - actionBarHeight - indicatorHeight - titleHeight - padding) / 8f) * 4f);
         width = height * (4f / 3f);

         thumbnailHeight = (1f / 3f) * height;
         thumbnailWidth = (thumbnailHeight * (4f / 3f));
      }

      // Set the width and height of the main image
      mLargeImage.getLayoutParams().height = (int) (height + .5f);
      mLargeImage.getLayoutParams().width = (int) (width + .5f);

      mImageOne.getLayoutParams().width = (int) thumbnailWidth;
      mImageOne.getLayoutParams().height = (int) thumbnailHeight;

      mImageTwo.getLayoutParams().width = (int) thumbnailWidth;
      mImageTwo.getLayoutParams().height = (int) thumbnailHeight;

      mImageThree.getLayoutParams().width = (int) thumbnailWidth;
      mImageThree.getLayoutParams().height = (int) thumbnailHeight;
   }

   public void setStepTitle(String title) {
      mTitle = title;
      if (mStepTitle != null)
         mStepTitle.setText(mTitle);
   }

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      savedInstanceState.putSerializable("" + IMAGE_KEY_1, mImageOneInfo);

      savedInstanceState.putSerializable("" + IMAGE_KEY_2, mImageTwoInfo);

      savedInstanceState.putSerializable("" + IMAGE_KEY_3, mImageThreeInfo);

      savedInstanceState.putString(TITLE_KEY, mTitle);
   }

   @Override
   public void onClick(View v) {
      String microURL = null;
      switch (v.getId()) {
         case R.id.step_edit_thumb_1:
            microURL = (String) mImageOne.getTag();
            break;
         case R.id.step_edit_thumb_2:
            microURL = (String) mImageTwo.getTag();
            break;
         case R.id.step_edit_thumb_3:
            microURL = (String) mImageThree.getTag();
            break;
         case R.id.step_edit_thumb_media:
            break;
         default:
            return;
      }
      if (microURL != null) {
         mImageManager.displayImage(microURL, getActivity(), mLargeImage);
         mLargeImage.invalidate();
      }
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //  super.onActivityResult(requestCode, resultCode, data);
      switch (requestCode) {
         case IMAGE_KEY_1:
            if (resultCode == Activity.RESULT_OK) {
               Log.e("LOL", " result meida result");
               MediaInfo media = (MediaInfo) data.getSerializableExtra(GalleryActivity.MEDIA_RETURN_KEY);
               mImageOneInfo.setText(media.getGuid());
               mImageOneInfo.setImageId(Integer.valueOf(media.getItemId()));
               setImage(IMAGE_KEY_1);
            }
            break;
         case IMAGE_KEY_2:
            if (resultCode == Activity.RESULT_OK) {
               MediaInfo media = (MediaInfo) data.getSerializableExtra(GalleryActivity.MEDIA_RETURN_KEY);
               mImageTwoInfo.setText(media.getGuid());
               mImageTwoInfo.setImageId(Integer.valueOf(media.getItemId()));
               setImage(IMAGE_KEY_2);
            }
            break;
         case IMAGE_KEY_3:
            if (resultCode == Activity.RESULT_OK) {
               MediaInfo media = (MediaInfo) data.getSerializableExtra(GalleryActivity.MEDIA_RETURN_KEY);
               mImageThreeInfo.setText(media.getGuid());
               mImageThreeInfo.setImageId(Integer.valueOf(media.getItemId()));
               setImage(IMAGE_KEY_3);
            }
            break;
      }
      setGuideDirty();
   }

   private void setImage(int location) {
      switch (location) {
         case 1:
            if(mImageOneInfo.getImageid() == NO_IMAGE) {
               mImageOne.setScaleType(ScaleType.CENTER);
               mImageOne.setImageResource(R.drawable.ic_btn_add_gallery_image);
               return;
            }
            mImageOne.setScaleType(ScaleType.FIT_CENTER);
            mImageManager.displayImage(mImageOneInfo.getText() + MainApplication.get().getImageSizes().getThumb(),
               getActivity(), mImageOne);
            mImageOne.setTag(mImageOneInfo.getText() + MainApplication.get().getImageSizes().getThumb());
            mImageOne.invalidate();

            mImageManager.displayImage(mImageOneInfo.getText() + MainApplication.get().getImageSizes().getThumb(), getActivity(), mLargeImage);
            mLargeImage.invalidate();
            break;
         case 2:
            if(mImageTwoInfo.getImageid() == NO_IMAGE) {
               mImageTwo.setScaleType(ScaleType.CENTER);
               mImageTwo.setImageResource(R.drawable.ic_btn_add_gallery_image);
               return;
            }
            mImageTwo.setScaleType(ScaleType.FIT_CENTER);
            mImageManager.displayImage(mImageTwoInfo.getText() + MainApplication.get().getImageSizes().getThumb(),
               getActivity(), mImageTwo);
            mImageTwo.setTag(mImageTwoInfo.getText() + MainApplication.get().getImageSizes().getThumb());
            mImageTwo.invalidate();

            mImageManager.displayImage((String) mImageTwo.getTag(), getActivity(), mLargeImage);
            mLargeImage.invalidate();
            break;
         case 3:
            if(mImageThreeInfo.getImageid() == NO_IMAGE){
               mImageThree.setScaleType(ScaleType.CENTER);
               mImageThree.setImageResource(R.drawable.ic_btn_add_gallery_image);
               return;
            }
            mImageThree.setScaleType(ScaleType.FIT_CENTER);
            mImageManager.displayImage(mImageThreeInfo.getText() + MainApplication.get().getImageSizes().getThumb(),
               getActivity(), mImageThree);
            mImageThree.setTag(mImageThreeInfo.getText() + MainApplication.get().getImageSizes().getThumb());
            mImageThree.invalidate();

            mImageManager.displayImage((String) mImageThree.getTag(), getActivity(), mLargeImage);
            mLargeImage.invalidate();
            break;
         default:
            return;
      }

   }

   public void setImage(int location, StepImage si) {
      switch (location) {
         case 1:
            mImageOneInfo = si;
            break;
         case 2:
            mImageTwoInfo = si;
            break;
         case 3:
            mImageThreeInfo = si;
            break;
         default:
            return;
      }

      if (mImageOne != null)
         setImage(location);
   }

   @Override
   public boolean onLongClick(View v) {
      Intent intent;
      switch (v.getId()) {
         case R.id.step_edit_thumb_1:
            if(mImageOneInfo.getImageid() != NO_IMAGE)
            {
               mCurSelectedKey = IMAGE_KEY_1;
               mQuickAction.show(v);
               return true;
            } 
            intent = new Intent(getActivity(), GalleryActivity.class);
            intent.putExtra(GalleryActivity.ACTIVITY_RETURN_MODE, 1);
            getActivity().startActivityForResult(intent, IMAGE_KEY_1);
            break;
         case R.id.step_edit_thumb_2:
            if(mImageTwoInfo.getImageid() != NO_IMAGE)
            {
               mCurSelectedKey = IMAGE_KEY_2;
               mQuickAction.show(v);
               return true;
            } 
            intent = new Intent(getActivity(), GalleryActivity.class);
            intent.putExtra(GalleryActivity.ACTIVITY_RETURN_MODE, 1);
            getActivity().startActivityForResult(intent, IMAGE_KEY_2);
            break;
         case R.id.step_edit_thumb_3:
            if(mImageThreeInfo.getImageid() != NO_IMAGE)
            {
               mCurSelectedKey = IMAGE_KEY_3;
               mQuickAction.show(v);
               return true;
            } 
            intent = new Intent(getActivity(), GalleryActivity.class);
            intent.putExtra(GalleryActivity.ACTIVITY_RETURN_MODE, 1);
            getActivity().startActivityForResult(intent, IMAGE_KEY_3);
            break;
         case R.id.step_edit_thumb_media:
            break;
      }
      return true;
   }

   public String getTitle() {
      return mTitle;
   }

   public ArrayList<StepImage> getImageIDs() {
      ArrayList<StepImage> list = new ArrayList<StepImage>();
      list.add(mImageOneInfo);
      list.add(mImageTwoInfo);
      list.add(mImageThreeInfo);
      return list;
   }
   
   public void setGuideDirty() {
      ((GuideStepChangedListener) getActivity()).onGuideStepChanged();
   }

}