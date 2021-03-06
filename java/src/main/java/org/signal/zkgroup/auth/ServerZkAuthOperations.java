// Generated by zkgroup/codegen/codegen.py - do not edit
package org.signal.zkgroup.auth;

import java.security.SecureRandom;
import java.util.UUID;
import org.signal.zkgroup.InvalidInputException;
import org.signal.zkgroup.ServerSecretParams;
import org.signal.zkgroup.VerificationFailedException;
import org.signal.zkgroup.InvalidRedemptionTimeException;
import org.signal.zkgroup.ZkGroupError;
import org.signal.zkgroup.groups.GroupPublicParams;
import org.signal.zkgroup.internal.Native;
import org.signal.zkgroup.util.UUIDUtil;

public class ServerZkAuthOperations {

  private final ServerSecretParams serverSecretParams;

  public ServerZkAuthOperations(ServerSecretParams serverSecretParams) {
    this.serverSecretParams = serverSecretParams;
  }

  public AuthCredentialResponse issueAuthCredential(UUID uuid, int redemptionTime) {
    return issueAuthCredential(new SecureRandom(), uuid, redemptionTime);
  }

  public AuthCredentialResponse issueAuthCredential(SecureRandom secureRandom, UUID uuid, int redemptionTime) {
    byte[] newContents = new byte[AuthCredentialResponse.SIZE];
    byte[] random      = new byte[Native.RANDOM_LENGTH];

    secureRandom.nextBytes(random);

    int ffi_return = Native.serverSecretParamsIssueAuthCredentialDeterministicJNI(serverSecretParams.getInternalContentsForJNI(), random, UUIDUtil.serialize(uuid), redemptionTime, newContents);

    if (ffi_return != Native.FFI_RETURN_OK) {
      throw new ZkGroupError("FFI_RETURN!=OK");
    }

    try {
      return new AuthCredentialResponse(newContents);
    } catch (InvalidInputException e) {
      throw new AssertionError(e);
    }

  }

  public void verifyAuthCredentialPresentation(GroupPublicParams groupPublicParams, AuthCredentialPresentation authCredentialPresentation) throws VerificationFailedException, InvalidRedemptionTimeException {
    verifyAuthCredentialPresentation(groupPublicParams, authCredentialPresentation, System.currentTimeMillis());
  }


  public void verifyAuthCredentialPresentation(GroupPublicParams groupPublicParams, AuthCredentialPresentation authCredentialPresentation, long currentTimeMillis) throws VerificationFailedException, InvalidRedemptionTimeException {

    long secondsPerDay = 24 * 3600;
    long redemptionDate = authCredentialPresentation.getRedemptionTime();

    long redemptionDayStartTime = redemptionDate * secondsPerDay;
    long redemptionDayEndTime = redemptionDayStartTime + secondsPerDay;

    long acceptableStartTime = redemptionDayStartTime - secondsPerDay;
    long acceptableEndTime = redemptionDayEndTime + secondsPerDay;

    if (currentTimeMillis < acceptableStartTime || currentTimeMillis > acceptableEndTime) {
        throw new InvalidRedemptionTimeException(); 
    }

    int ffi_return = Native.serverSecretParamsVerifyAuthCredentialPresentationJNI(serverSecretParams.getInternalContentsForJNI(), groupPublicParams.getInternalContentsForJNI(), authCredentialPresentation.getInternalContentsForJNI());
    if (ffi_return == Native.FFI_RETURN_INPUT_ERROR) {
      throw new VerificationFailedException();
    }

    if (ffi_return != Native.FFI_RETURN_OK) {
      throw new ZkGroupError("FFI_RETURN!=OK");
    }
  }

}
