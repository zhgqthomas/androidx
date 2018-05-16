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

package androidx.media.test.client;

import static android.support.mediacompat.testlib.util.IntentUtil.SERVICE_PACKAGE_NAME;

import static androidx.media.test.lib.CommonConstants.DEFAULT_TEST_NAME;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import androidx.media.MediaController2;
import androidx.media.SessionToken2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Executor;

/** Test {@link ClientTestHelper}. */
@RunWith(AndroidJUnit4.class)
public class ClientTestHelperTest {
    private static final int TIME_OUT_MS = 3000;

    private Context mContext;
    private ClientTestHelper mTestHelper;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        mTestHelper = new ClientTestHelper(mContext);
        boolean connected = mTestHelper.connect(TIME_OUT_MS);
        if (!connected) {
            fail("Failed to connect to TestHelperService.");
        }
    }

    @After
    public void cleanUp() {
        mTestHelper.disconnect(true);
    }

    @Test
    @SmallTest
    public void testGettingToken() {
        SessionToken2 token = mTestHelper.createMediaSession2(DEFAULT_TEST_NAME);
        assertNotNull(token);
        assertEquals(SERVICE_PACKAGE_NAME, token.getPackageName());
    }

    @Test
    @SmallTest
    public void testCreatingController() {
        Looper.prepare();
        SessionToken2 token = mTestHelper.createMediaSession2(DEFAULT_TEST_NAME);
        assertNotNull(token);
        MediaController2 controller = new MediaController2(mContext, token, new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        }, new MediaController2.ControllerCallback() {});
    }
}
