
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.androidPushNotification = functions.firestore.document("Notifications/{docID}").onCreate(
    (snapshot, context) => {
        admin.messaging().sendToTopic(
            "new_user_forms",
            {
                notification: {
                    title: "Baby Sense",
                    body: "NOTTUT",
                }
            }
        );
    }
);