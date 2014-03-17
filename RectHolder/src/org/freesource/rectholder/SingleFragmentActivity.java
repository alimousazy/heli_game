package org.freesource.rectholder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {

	FragmentManager fm;
	protected Fragment mFragment;

	abstract public Fragment getFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.frag_contaner);
		if (fragment == null) {
			mFragment = getFragment();
			fm.beginTransaction().add(R.id.frag_contaner, mFragment).commit();
		}

	}

}
