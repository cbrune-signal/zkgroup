// Generated by zkgroup/codegen/codegen.py - do not edit
package org.signal.zkgroup.profiles;

import org.signal.zkgroup.InvalidInputException;
import org.signal.zkgroup.ZkGroupError;
import org.signal.zkgroup.internal.ByteArray;
import org.signal.zkgroup.internal.Native;

public final class ProfileKeyCommitment extends ByteArray {

  public static final int SIZE = 64;

  public ProfileKeyCommitment(byte[] contents) throws InvalidInputException {
    super(contents, SIZE);
    
    int ffi_return = Native.profileKeyCommitmentCheckValidContentsJNI(contents);

    if (ffi_return == Native.FFI_RETURN_INPUT_ERROR) {
      throw new InvalidInputException("FFI_RETURN_INPUT_ERROR");
    }

    if (ffi_return != Native.FFI_RETURN_OK) {
      throw new ZkGroupError("FFI_RETURN!=OK");
    }
  }

  public ProfileKeyVersion getProfileKeyVersion() {
    byte[] newContents = new byte[ProfileKeyVersion.SIZE];

    int ffi_return = Native.profileKeyCommitmentGetProfileKeyVersionJNI(contents, newContents);

    if (ffi_return != Native.FFI_RETURN_OK) {
      throw new ZkGroupError("FFI_RETURN!=OK");
    }

    try {
      return new ProfileKeyVersion(newContents);
    } catch (InvalidInputException e) {
      throw new AssertionError(e);
    }

  }

  public byte[] serialize() {
    return contents.clone();
  }

}
