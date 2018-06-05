/**
 * Copyright @ 2008 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package net.Mchien.NhanDangCongVan.wia;

/**
 *
 * @author Quan
 */
public enum WiaScannerError {

    LibraryNotInstalled(0x80040154), OutputFileExists(0x80070050), ScannerNotAvailable(0x80210015), OperationCancelled(0x80210064);

    private final long value;

    WiaScannerError(long value) {
        this.value = value;
    }

    long getValue() {
        return value;
    }
}