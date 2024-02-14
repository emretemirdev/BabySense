const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationOnDataChangev2 = functions.database.ref('/sensorData/{id}')
    .onWrite((change, context) => {
        // Veri değişikliğini algıla
        const afterData = change.after.val(); // Güncellenen veri
        const beforeData = change.before.val(); // Önceki veri

        // Basit bir kontrol, veri değişmiş mi diye
        if (afterData !== beforeData) {
            const payload = {
                notification: {
                    title: 'Data Updated',
                    body: 'Sensor data has been updated.'
                }
            };

            // Tüm kullanıcılara bildirim gönder
            return admin.messaging().sendToTopic('sensorUpdates', payload)
                .then(response => {
                    console.log('Notification sent successfully:', response);
                    return null;
                })
                .catch(error => {
                    console.log('Notification sent failed:', error);
                    return null;
                });
        } else {
            return null;
        }
    });
