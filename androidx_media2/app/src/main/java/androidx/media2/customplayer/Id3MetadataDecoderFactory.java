/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.media2.customplayer;


import android.annotation.SuppressLint;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataDecoderFactory;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.Arrays;

/**
 * Factory for metadata decoders that provide raw ID3 data in {@link androidx.media2.customplayer.ByteArrayFrame}s.
 */
@SuppressLint("RestrictedApi") // TODO(b/68398926): Remove once RestrictedApi checks are fixed.
/* package */ final class Id3MetadataDecoderFactory implements MetadataDecoderFactory {

    @Override
    public boolean supportsFormat(Format format) {
        return MimeTypes.APPLICATION_ID3.equals(format.sampleMimeType);
    }

    @Override
    public MetadataDecoder createDecoder(Format format) {
        return new MetadataDecoder() {
            @Override
            public Metadata decode(MetadataInputBuffer inputBuffer) {
                long timestamp = inputBuffer.timeUs;
                byte[] bufferData = inputBuffer.data.array();
                Metadata.Entry entry =
                        new androidx.media2.customplayer.ByteArrayFrame(timestamp, Arrays.copyOf(bufferData, bufferData.length));
                return new Metadata(entry);
            }
        };
    }

}
