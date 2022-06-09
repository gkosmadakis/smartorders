

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
// The stripe API
const stripe = require('stripe')('sk_test_50wnmiroW2ia9FhnXBPj2slO00E1JGcvFg');
admin.initializeApp();


	
// Function to create a customer when they sign in Firebase for the first time
exports.createStripeCustomer = functions.auth.user().onCreate(async (user) => {
    const customer = await stripe.customers.create({email: user.email});
    return admin.firestore().collection('stripe_customers')
        .doc(user.uid).set({customer_id: customer.id});
});

//Create a Stripe Payment Method
exports.addPaymentSource = functions.firestore
    .document('/stripe_customers/{userId}/tokens/{pushId}')
    .onWrite(async (change, context) => {
    const source = change.after.data();
    const token = source.token;
    if (source === null) {
        return null;
    }
    try {
    const snapshot = await
    admin.firestore()
    .collection('stripe_customers')
    .doc(context.params.userId)
    .get();
    const customerId = snapshot.data().customer_id;
    const response = await stripe.customers
        .createSource(customerId, {source: token});
	await stripe.customers.update(customerId,
	  {default_source: response.id });
    return admin.firestore()
    .collection('stripe_customers')
    .doc(context.params.userId)
    .collection('sources')
    .doc(response.fingerprint)
    .set(response, {merge: true});
    } catch (error) {
    await change.after.ref
        .set({'error':userFacingMessage(error)},{merge:true});
		console.log('Error in AddPaymentSource');
		console.error(error);
    }
});


//Make a Payment
exports.createStripeCharge = functions.firestore
    .document('stripe_customers/{userId}/charges/{id}')
    .onCreate(async (snap, context) => {
    const val = snap.data();
    try {
    // Look up the Stripe customer id written in createStripeCustomer
    const snapshot = await admin.firestore()
    .collection('stripe_customers')
    .doc(context.params.userId).get();
    
    const snapval = snapshot.data();
    const customer = snapval.customer_id;
    // Create a charge using the pushId as the idempotency key
    // protecting against double charges
    const amount = val.amount;
	const currency = val.currency;
    const idempotencyKey = context.params.id;
    const charge = {amount, currency, customer};
    if (val.source !== null) {
       charge.source = val.source;
    }
    const response = await stripe.charges
        .create(charge, {idempotency_key: idempotencyKey});
    // If the result is successful, write it back to the database
    return snap.ref.set(response, { merge: true });
    } catch(error) {
		console.log('Error in createStripeCharge');
		console.error(error);
        await snap.ref.set({error: userFacingMessage(error)}, { merge: true });
    }
});

// Sanitize the error message for the user
function userFacingMessage(error) {
  return error.type ? error.message : 'An error occurred, developers have been alerted';
}