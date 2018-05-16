/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.media.test.service;

import static android.support.mediacompat.testlib.util.IntentUtil.CLIENT_PACKAGE_NAME;

import static androidx.media.test.lib.CommonConstants.ACTION_TEST_HELPER;
import static androidx.media.test.lib.CommonConstants.INDEX_FOR_NULL_DSD;
import static androidx.media.test.lib.CommonConstants.INDEX_FOR_UNKONWN_DSD;
import static androidx.media.test.lib.CommonConstants.KEY_ARGUMENTS;
import static androidx.media.test.lib.CommonConstants.KEY_AUDIO_ATTRIBUTES;
import static androidx.media.test.lib.CommonConstants.KEY_BUFFERED_POSITION;
import static androidx.media.test.lib.CommonConstants.KEY_BUFFERING_STATE;
import static androidx.media.test.lib.CommonConstants.KEY_COMMAND_BUTTON_LIST;
import static androidx.media.test.lib.CommonConstants.KEY_COMMAND_GROUP;
import static androidx.media.test.lib.CommonConstants.KEY_CURRENT_POSITION;
import static androidx.media.test.lib.CommonConstants.KEY_CURRENT_VOLUME;
import static androidx.media.test.lib.CommonConstants.KEY_CUSTOM_COMMAND;
import static androidx.media.test.lib.CommonConstants.KEY_DURATION;
import static androidx.media.test.lib.CommonConstants.KEY_ERROR_CODE;
import static androidx.media.test.lib.CommonConstants.KEY_EXTRAS;
import static androidx.media.test.lib.CommonConstants.KEY_ITEM_INDEX;
import static androidx.media.test.lib.CommonConstants.KEY_MAX_VOLUME;
import static androidx.media.test.lib.CommonConstants.KEY_MEDIA_ITEM;
import static androidx.media.test.lib.CommonConstants.KEY_PLAYER_STATE;
import static androidx.media.test.lib.CommonConstants.KEY_PLAYLIST;
import static androidx.media.test.lib.CommonConstants.KEY_PLAYLIST_METADATA;
import static androidx.media.test.lib.CommonConstants.KEY_REPEAT_MODE;
import static androidx.media.test.lib.CommonConstants.KEY_RESULT_RECEIVER;
import static androidx.media.test.lib.CommonConstants.KEY_ROUTE_LIST;
import static androidx.media.test.lib.CommonConstants.KEY_SEEK_POSITION;
import static androidx.media.test.lib.CommonConstants.KEY_SHUFFLE_MODE;
import static androidx.media.test.lib.CommonConstants.KEY_SPEED;
import static androidx.media.test.lib.CommonConstants.KEY_STREAM;
import static androidx.media.test.lib.CommonConstants.KEY_VOLUME_CONTROL_TYPE;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .NOTIFY_BUFFERED_STATE_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .NOTIFY_CURRENT_DATA_SOURCE_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .NOTIFY_MEDIA_PREPARED;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .NOTIFY_PLAYBACK_SPEED_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .NOTIFY_PLAYER_STATE_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .NOTIFY_SEEK_COMPLETED;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .SET_BUFFERED_POSITION_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .SET_CURRENT_MEDIA_ITEM_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .SET_CURRENT_POSITION_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .SET_DURATION_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.BaseMediaPlayerMethods
        .SET_PLAYBACK_SPEED_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .NOTIFY_PLAYLIST_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .NOTIFY_PLAYLIST_METADATA_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .NOTIFY_REPEAT_MODE_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .NOTIFY_SHUFFLE_MODE_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .SET_PLAYLIST_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .SET_PLAYLIST_METADATA_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .SET_REPEAT_MODE_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.PlaylistAgentMethods
        .SET_SHUFFLE_MODE_MANUALLY;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods.CLOSE;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods
        .CUSTOM_METHOD_SET_MULTIPLE_VALUES;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods.NOTIFY_ERROR;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods
        .NOTIFY_ROUTES_INFO_CHANGED;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods.SEND_CUSTOM_COMMAND;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods
        .SEND_CUSTOM_COMMAND_TO_ONE_CONTROLLER;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods.SET_ALLOWED_COMMANDS;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods.SET_CUSTOM_LAYOUT;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods.UPDATE_PLAYER;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods
        .UPDATE_PLAYER_FOR_SETTING_STREAM_TYPE;
import static androidx.media.test.lib.MediaSession2Constants.Session2Methods
        .UPDATE_PLAYER_WITH_VOLUME_PROVIDER;
import static androidx.media.test.lib.MediaSession2Constants
        .TEST_CONTROLLER_CALLBACK_SESSION_REJECTS;
import static androidx.media.test.lib.MediaSession2Constants.TEST_GET_SESSION_ACTIVITY;
import static androidx.media.test.lib.MediaSession2Constants
        .TEST_ON_PLAYLIST_METADATA_CHANGED_SESSION_SET_PLAYLIST;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.mediacompat.testlib.IServiceAppTestHelperService;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media.AudioAttributesCompat;
import androidx.media.DataSourceDesc;
import androidx.media.MediaItem2;
import androidx.media.MediaMetadata2;
import androidx.media.MediaSession2;
import androidx.media.MediaSession2.CommandButton;
import androidx.media.SessionCommand2;
import androidx.media.SessionCommandGroup2;
import androidx.media.VolumeProviderCompat;
import androidx.media.test.lib.MockActivity;
import androidx.media.test.lib.TestUtils.SyncHandler;

import java.io.FileDescriptor;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * A Service that creates {@link MediaSession2} and calls its methods according to the client app's
 * requests.
 */
public class TestHelperService extends Service {

    private static final String TAG = "TestHelperService_serviceApp";

    MediaSession2 mSession2;
    MockPlayer mPlayer;
    MockPlaylistAgent mPlaylistAgent;
    ServiceBinder mBinder;

    SyncHandler mHandler;
    Executor mExecutor;

    MediaSession2.ControllerInfo mControllerInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new ServiceBinder();
        mHandler = new SyncHandler(getMainLooper());
        mExecutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                mHandler.post(command);
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (ACTION_TEST_HELPER.equals(intent.getAction())) {
            return mBinder;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        if (mSession2 != null) {
            mSession2.close();
        }
    }

    private class ServiceBinder extends IServiceAppTestHelperService.Stub {
        @Override
        public Bundle createMediaSession2(String sessionId) throws RemoteException {
            if (mSession2 != null) {
                mSession2.close();
            }

            mPlayer = new MockPlayer(0);
            mPlaylistAgent = new MockPlaylistAgent();
            final MediaSession2.Builder builder = new MediaSession2.Builder(TestHelperService.this)
                    .setId(sessionId)
                    .setPlayer(mPlayer)
                    .setPlaylistAgent(mPlaylistAgent);

            switch (sessionId) {
                case TEST_GET_SESSION_ACTIVITY: {
                    final Intent sessionActivity = new Intent(TestHelperService.this,
                            MockActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(TestHelperService.this,
                            0 /* requestCode */, sessionActivity, 0 /* flags */);
                    builder.setSessionActivity(pendingIntent);
                    break;
                }
                case TEST_CONTROLLER_CALLBACK_SESSION_REJECTS: {
                    builder.setSessionCallback(mExecutor, new MediaSession2.SessionCallback() {
                        @Override
                        public SessionCommandGroup2 onConnect(MediaSession2 session,
                                MediaSession2.ControllerInfo controller) {
                            return null;
                        }
                    });
                    break;
                }
                case TEST_ON_PLAYLIST_METADATA_CHANGED_SESSION_SET_PLAYLIST: {
                    builder.setSessionCallback(mExecutor, new MediaSession2.SessionCallback() {
                        @Override
                        public SessionCommandGroup2 onConnect(MediaSession2 session,
                                MediaSession2.ControllerInfo controller) {
                            SessionCommandGroup2 commands = new SessionCommandGroup2();
                            commands.addCommand(new SessionCommand2(
                                    SessionCommand2.COMMAND_CODE_PLAYLIST_GET_LIST_METADATA));
                            return commands;
                        }
                    });
                    break;
                }
            }

            try {
                mHandler.postAndSync(new Runnable() {
                    @Override
                    public void run() {
                        mSession2 = builder.build();
                    }
                });
            } catch (InterruptedException ex) {
                Log.e(TAG, "InterruptedException occurred while creating MediaSession2", ex);
            }

            return mSession2.getToken().toBundle();
        }

        @Override
        public void closeMediaSession2() throws RemoteException {
            if (mSession2 != null) {
                mSession2.close();
            }
        }

        @Override
        public void callMediaSession2Method(int method, @NonNull Bundle args)
                throws RemoteException {
            args.setClassLoader(MediaSession2.class.getClassLoader());

            switch (method) {
                // TODO: Unite UPDATE_PLAYER* constants into one which can be generally used.
                case UPDATE_PLAYER: {
                    MockPlayer player = new MockPlayer(0);
                    player.mLastPlayerState = args.getInt(KEY_PLAYER_STATE);
                    player.setAudioAttributes(AudioAttributesCompat.fromBundle(
                            (Bundle) args.getParcelable(KEY_AUDIO_ATTRIBUTES)));

                    MockPlaylistAgent agent = new MockPlaylistAgent();
                    agent.mPlaylist = MediaTestUtils.playlistFromParcelableList(
                            args.getParcelableArrayList(KEY_PLAYLIST), false /* createDsd */);

                    mSession2.updatePlayer(player, agent, null);
                    break;
                }
                case UPDATE_PLAYER_FOR_SETTING_STREAM_TYPE: {
                    // Set stream of the session.
                    final int stream = args.getInt(KEY_STREAM);
                    AudioAttributesCompat attrs = new AudioAttributesCompat.Builder()
                            .setLegacyStreamType(stream)
                            .build();
                    MockPlayer player = new MockPlayer(0);
                    player.setAudioAttributes(attrs);
                    mSession2.updatePlayer(player, null, null);
                    break;
                }
                case UPDATE_PLAYER_WITH_VOLUME_PROVIDER: {
                    VolumeProviderCompat vp = new VolumeProviderCompat(
                            args.getInt(KEY_VOLUME_CONTROL_TYPE),
                            args.getInt(KEY_MAX_VOLUME),
                            args.getInt(KEY_CURRENT_VOLUME)) {};

                    MockPlayer player = new MockPlayer(0);
                    player.setAudioAttributes(AudioAttributesCompat.fromBundle(
                            (Bundle) args.getParcelable(KEY_AUDIO_ATTRIBUTES)));
                    mSession2.updatePlayer(player, null, vp);
                    break;
                }
                case SEND_CUSTOM_COMMAND: {
                    SessionCommand2 customCommand =
                            SessionCommand2.fromBundle(args.getBundle(KEY_CUSTOM_COMMAND));
                    Bundle arguments = args.getBundle(KEY_ARGUMENTS);
                    mSession2.sendCustomCommand(customCommand, arguments);
                    break;
                } case SEND_CUSTOM_COMMAND_TO_ONE_CONTROLLER: {
                    SessionCommand2 customCommand =
                            SessionCommand2.fromBundle(args.getBundle(KEY_CUSTOM_COMMAND));
                    Bundle arguments = args.getBundle(KEY_ARGUMENTS);
                    ResultReceiver resultReceiver = args.getParcelable(KEY_RESULT_RECEIVER);
                    mSession2.sendCustomCommand(
                            getTestControllerInfo(), customCommand, arguments, resultReceiver);
                    break;
                }
                case CLOSE: {
                    mSession2.close();
                    break;
                }
                case NOTIFY_ERROR: {
                    int errorCode = args.getInt(KEY_ERROR_CODE);
                    Bundle extras = args.getBundle(KEY_EXTRAS);
                    mSession2.notifyError(errorCode, extras);
                    break;
                }
                case SET_ALLOWED_COMMANDS: {
                    SessionCommandGroup2 commandGroup =
                            SessionCommandGroup2.fromBundle(args.getBundle(KEY_COMMAND_GROUP));
                    mSession2.setAllowedCommands(getTestControllerInfo(), commandGroup);
                    break;
                }
                case NOTIFY_ROUTES_INFO_CHANGED: {
                    List<Bundle> routes = args.getParcelableArrayList(KEY_ROUTE_LIST);
                    mSession2.notifyRoutesInfoChanged(getTestControllerInfo(), routes);
                    break;
                }
                case SET_CUSTOM_LAYOUT: {
                    List<CommandButton> buttons = MediaTestUtils.buttonListFromParcelableArrayList(
                            args.getParcelableArrayList(KEY_COMMAND_BUTTON_LIST));
                    mSession2.setCustomLayout(getTestControllerInfo(), buttons);
                    break;
                }
                case CUSTOM_METHOD_SET_MULTIPLE_VALUES: {
                    mPlayer.mLastPlayerState = args.getInt(KEY_PLAYER_STATE);
                    mPlayer.mLastBufferingState = args.getInt(KEY_BUFFERING_STATE);
                    mPlayer.mCurrentPosition = args.getLong(KEY_CURRENT_POSITION);
                    mPlayer.mBufferedPosition = args.getLong(KEY_BUFFERED_POSITION);
                    mPlayer.mPlaybackSpeed = args.getFloat(KEY_SPEED);
                    mPlaylistAgent.mCurrentMediaItem =
                            MediaItem2.fromBundle(args.getBundle(KEY_MEDIA_ITEM));
                    break;
                }
            }

        }

        @Override
        public void callBaseMediaPlayerMethod(int method, @NonNull Bundle args)
                throws RemoteException {
            if (mSession2 == null) {
                Log.d(TAG, "callBaseMediaPlayerMethod() called when session is null.");
                return;
            }

            switch (method) {
                case SET_CURRENT_POSITION_MANUALLY: {
                    mPlayer.mCurrentPosition = args.getLong(KEY_CURRENT_POSITION);
                    break;
                }
                case NOTIFY_SEEK_COMPLETED: {
                    mPlayer.notifySeekCompleted(args.getLong(KEY_SEEK_POSITION));
                    break;
                }
                case SET_BUFFERED_POSITION_MANUALLY: {
                    mPlayer.mBufferedPosition = args.getLong(KEY_BUFFERED_POSITION);
                    break;
                }
                case NOTIFY_BUFFERED_STATE_CHANGED: {
                    final int index = args.getInt(KEY_ITEM_INDEX);
                    DataSourceDesc dsd =
                            mPlaylistAgent.getPlaylist().get(index).getDataSourceDesc();
                    mPlayer.notifyBufferingStateChanged(dsd, args.getInt(KEY_BUFFERING_STATE));
                    break;
                }
                case NOTIFY_PLAYER_STATE_CHANGED: {
                    mPlayer.notifyPlayerStateChanged(args.getInt(KEY_PLAYER_STATE));
                    break;
                }
                case NOTIFY_CURRENT_DATA_SOURCE_CHANGED: {
                    int itemIndex = args.getInt(KEY_ITEM_INDEX);
                    switch (itemIndex) {
                        case INDEX_FOR_UNKONWN_DSD:
                            mPlayer.notifyCurrentDataSourceChanged(new DataSourceDesc.Builder()
                                            .setDataSource(new FileDescriptor())
                                            .build());
                            break;
                        case INDEX_FOR_NULL_DSD:
                            mPlayer.notifyCurrentDataSourceChanged(null);
                            break;
                        default:
                            mPlayer.notifyCurrentDataSourceChanged(mPlaylistAgent.getPlaylist()
                                    .get(itemIndex).getDataSourceDesc());
                            break;
                    }
                    break;
                }
                case SET_PLAYBACK_SPEED_MANUALLY: {
                    mPlayer.mPlaybackSpeed = args.getFloat(KEY_SPEED);
                    break;
                }
                case NOTIFY_PLAYBACK_SPEED_CHANGED: {
                    mPlayer.notifyPlaybackSpeedChanged(args.getFloat(KEY_SPEED));
                    break;
                }
                case SET_DURATION_MANUALLY: {
                    mPlayer.mDuration = args.getLong(KEY_DURATION);
                    break;
                }
                case NOTIFY_MEDIA_PREPARED: {
                    int itemIndex = args.getInt(KEY_ITEM_INDEX);
                    mPlayer.notifyMediaPrepared(
                            mPlaylistAgent.mPlaylist.get(itemIndex).getDataSourceDesc());
                    break;
                }
            }
        }

        @Override
        public void callMediaPlaylistAgentMethod(int method, @NonNull Bundle args)
                throws RemoteException {
            if (mSession2 == null) {
                Log.d(TAG, "callMediaPlaylistAgentMethod() called when session is null.");
                return;
            }

            switch (method) {
                case SET_PLAYLIST_MANUALLY: {
                    List<MediaItem2> playlist = MediaTestUtils.playlistFromParcelableList(
                            args.getParcelableArrayList(KEY_PLAYLIST), true /* createDsd */);
                    mPlaylistAgent.mPlaylist = playlist;
                    break;
                }
                case NOTIFY_PLAYLIST_CHANGED: {
                    mPlaylistAgent.notifyPlaylistChanged();
                    break;
                }
                case SET_PLAYLIST_METADATA_MANUALLY: {
                    mPlaylistAgent.mMetadata = MediaMetadata2.fromBundle(
                            args.getBundle(KEY_PLAYLIST_METADATA));
                    break;
                }
                case NOTIFY_PLAYLIST_METADATA_CHANGED: {
                    mPlaylistAgent.notifyPlaylistMetadataChanged();
                    break;
                }
                case SET_SHUFFLE_MODE_MANUALLY: {
                    mPlaylistAgent.mShuffleMode = args.getInt(KEY_SHUFFLE_MODE);
                    break;
                }
                case NOTIFY_SHUFFLE_MODE_CHANGED: {
                    mPlaylistAgent.notifyShuffleModeChanged();
                    break;
                }
                case SET_REPEAT_MODE_MANUALLY: {
                    mPlaylistAgent.mRepeatMode = args.getInt(KEY_REPEAT_MODE);
                    break;
                }
                case NOTIFY_REPEAT_MODE_CHANGED: {
                    mPlaylistAgent.notifyRepeatModeChanged();
                    break;
                }
                case SET_CURRENT_MEDIA_ITEM_MANUALLY: {
                    int itemIndex = args.getInt(KEY_ITEM_INDEX);
                    mPlaylistAgent.mCurrentMediaItem = mPlaylistAgent.mPlaylist.get(itemIndex);
                    break;
                }
            }
        }

        private MediaSession2.ControllerInfo getTestControllerInfo() {
            if (mSession2 == null) {
                return null;
            }
            for (MediaSession2.ControllerInfo info : mSession2.getConnectedControllers()) {
                if (CLIENT_PACKAGE_NAME.equals(info.getPackageName())) {
                    return info;
                }
            }
            Log.e(TAG, "Test controller was not found in connected controllers.");
            return null;
        }
    }
}
