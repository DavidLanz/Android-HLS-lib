package com.kaltura.hlsplayersdk.subtitles;

import java.util.Vector;

import com.kaltura.hlsplayersdk.manifest.ManifestParser;
import com.kaltura.hlsplayersdk.manifest.ManifestPlaylist;

import android.util.Log;

public class SubtitleHandler {

	private ManifestParser mManifest;
	private double mLastTime = 0;
	
	public SubtitleHandler(ManifestParser baseManifest)
	{
		mManifest = baseManifest;
	}
	
	public boolean hasSubtitles()
	{
		boolean rval = mManifest.subtitles.size() > 0;
		if (!rval) rval = mManifest.subtitlePlayLists.size() > 0;			
		return rval;
	}
	
	public String[] getLanguages()
	{
		if (mManifest.subtitlePlayLists.size() > 0)
		{
			String[] languages = new String[mManifest.subtitlePlayLists.size()];
			for (int i = 0; i < mManifest.subtitlePlayLists.size(); ++i)
			{
				languages[i] = mManifest.subtitlePlayLists.get(i).language;
			}
			return languages;
		}
		return null;
	}
	
	public int getDefaultLanguageIndex()
	{
		for (int i = 0; i < mManifest.subtitlePlayLists.size(); ++i)
		{
			if (mManifest.subtitlePlayLists.get(i).isDefault)
				return i;
		}
		return 0;		
	}
	
	public int getLanguageCount()
	{
		if (mManifest.subtitlePlayLists.size() > 0) return mManifest.subtitlePlayLists.size();
		if (mManifest.subtitles.size() > 0) return 1;
		return 0;
	}
	
	public Vector<TextTrackCue> update(double time, int language)
	{
		SubTitleParser stp = getParserForTime(time, language);
		
		if (stp != null)
		{
			Vector<TextTrackCue> cues = stp.getCuesForTimeRange(mLastTime, time);
			mLastTime = time;
			
			return cues;
		}
		return null;
	}
	
	private SubTitleParser getParserForTime(double time, int language)
	{
		ManifestParser mp = null;
		if (mManifest.subtitlePlayLists.size() > language)
		{
			
			ManifestPlaylist mpl = mManifest.subtitlePlayLists.get(language);
			if (mpl.manifest.subtitles.size() > 0)
				mp = mpl.manifest;
		}
		else if (mManifest.subtitles.size() > 0)
		{
			mp = mManifest;
		}
		
		for (int i = 0; i < mp.subtitles.size(); ++i)
		{
			SubTitleParser stp = mp.subtitles.get(i);
			if (stp != null)
			{
				if (time >= stp.startTime && time <= stp.endTime)
				{
					return stp;
				}
			}
		}
		return null;

	}
}