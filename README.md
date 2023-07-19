Hi everyone, this is a small project - Template, for all my future projects.

Here in particular implemented Spring Security on Spring 3.1.1

How to start the project?

 - You will need an IDE to run java applications

 - Google account, since JavaMailSender is used to send requests through gmail.
You will need to go to google.ru or com -> Account Management -> Security -> Two-step Authentication -> Scroll to the bottom and find "Application Passwords" -> Create a password with the Mail type and the device on which you will run the project.

 - Google will give you the password which you should put in two places - application.yml and JavaMailSender located in SecurityApplication(Class annotated @SpringBootApplication).

 - Postgres database and all

 - Add the USER role to the role table


The capabilities of this application are:

 - Standard Login
 - Logout
 - Registration with sending email
 - Reset Password with sending email.

How does it work?

``ApplicationEventPublisher`` is used for this, when it receives a post request for registration or password reset, it triggers the event.

Example:

``eventPublisher.publishEvent(new OnRegistrationCompleteEvent(newUser, request.getLocale(), appUrl));``


This triggers the PasswordResetListener which sends an email.

The rest of the logic is pretty straightforward.