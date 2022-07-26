package com.bayzid.qrbarcodescanner2022.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bayzid.qrbarcodescanner2022.Activity.PrivacyPolicyActivity;
import com.bayzid.qrbarcodescanner2022.Activity.ReferActivity;
import com.bayzid.qrbarcodescanner2022.Adapter.HistoryAdapter;
import com.bayzid.qrbarcodescanner2022.R;
import com.bayzid.qrbarcodescanner2022.constant.PreferenceKey;
import com.bayzid.qrbarcodescanner2022.databinding.FragmentSettingsBinding;
import com.bayzid.qrbarcodescanner2022.utils.SharedPrefUtil;

import io.reactivex.disposables.CompositeDisposable;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{
    private FragmentSettingsBinding mBinding;

    public SettingsFragment() {

    }
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        loadSettings();
        setListeners();
        return mBinding.getRoot();
    }

    private void loadSettings() {
        mBinding.switchCompatPlaySound.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.PLAY_SOUND));
        mBinding.switchCompatVibrate.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.VIBRATE));
        mBinding.switchCompatSaveHistory.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.SAVE_HISTORY));
        mBinding.switchCompatCopyToClipboard.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.COPY_TO_CLIPBOARD));
    }

    private void setListeners() {
        mBinding.switchCompatPlaySound.setOnCheckedChangeListener(this);
        mBinding.switchCompatVibrate.setOnCheckedChangeListener(this);
        mBinding.switchCompatSaveHistory.setOnCheckedChangeListener(this);
        mBinding.switchCompatCopyToClipboard.setOnCheckedChangeListener(this);

        mBinding.textViewPlaySound.setOnClickListener(this);
        mBinding.textViewVibrate.setOnClickListener(this);
        mBinding.textViewSaveHistory.setOnClickListener(this);
        mBinding.textViewCopyToClipboard.setOnClickListener(this);
        mBinding.textViewPrivacyPolicy.setOnClickListener(this);
        mBinding.textViewRefer.setOnClickListener(this);
        mBinding.textViewRateUs.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_compat_play_sound:
                SharedPrefUtil.write(PreferenceKey.PLAY_SOUND, isChecked);
                break;

            case R.id.switch_compat_vibrate:
                SharedPrefUtil.write(PreferenceKey.VIBRATE, isChecked);
                break;

            case R.id.switch_compat_save_history:
                SharedPrefUtil.write(PreferenceKey.SAVE_HISTORY, isChecked);
                break;

            case R.id.switch_compat_copy_to_clipboard:
                SharedPrefUtil.write(PreferenceKey.COPY_TO_CLIPBOARD, isChecked);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_play_sound:
                mBinding.switchCompatPlaySound.setChecked(!mBinding.switchCompatPlaySound.isChecked());
                break;

            case R.id.text_view_vibrate:
                mBinding.switchCompatVibrate.setChecked(!mBinding.switchCompatVibrate.isChecked());
                break;

            case R.id.text_view_save_history:
                mBinding.switchCompatSaveHistory.setChecked(!mBinding.switchCompatSaveHistory.isChecked());
                break;

            case R.id.text_view_copy_to_clipboard:
                mBinding.switchCompatCopyToClipboard.setChecked(!mBinding.switchCompatCopyToClipboard.isChecked());
                break;
            case R.id.text_view_privacy_policy:
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
                break;
            case R.id.text_view_refer:
                startActivity(new Intent(getActivity(), ReferActivity.class));
                break;
            case R.id.text_view_rate_us:
                RateUs();
                break;
            default:
                break;
        }
    }

    private void RateUs() {
        try{
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.bayzid.qrbarcodescanner2022")));
        }catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.simplemobiletools.gallery.bayzid")));
//            https://play.google.com/store/apps/dev?id=6496719373857236664
        }
    }
}