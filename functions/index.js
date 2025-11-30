const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

// Force region to asia-southeast1 to match the app and database
exports.issueCustomToken = functions.region('asia-southeast1').https.onCall(async (data, context) => {
  const deviceId = data.deviceId;

  if (!deviceId) {
    throw new functions.https.HttpsError('invalid-argument', 'The function must be called with one argument "deviceId".');
  }

  try {
    const customToken = await admin.auth().createCustomToken(deviceId);
    return { token: customToken };
  } catch (error) {
    console.error("Error creating custom token:", error);
    throw new functions.https.HttpsError('internal', 'Unable to create custom token');
  }
});
