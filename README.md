# Skillbook
Developed a prototype during an internship.
Through this app, a user can login as a trainer as well as as a trainee.<br>
As a trainer, he can advertise his training classes, by providing all the necessary details regarding the classes.<br>
As a trainee, he can browse for his desired training classes and get himself enrolled in those classes.<br>

Some important java files in the app:-

## 1. all_train_Sqlite:-
sqlite database that contains detaiils of all the trainings 

## 2. crt_training:-
activity to create training

## 3. HomeActivity:-
This is the first that opens when a user logs in

## 4. item:-
this is the class of  recyclerview list items in MainActivity and Main2Activity

## 5. Login:-
login activity
(if a user logs in 'using google' for the first time then he will be registered in database with a unique user_id and  g-mail id)   

## 6. Main2Activity:-
this activity contains recyclerview with all trainings

## 7. MainActivity:-
this activity contains 3 tabs :-

    a.    My profile:- this tab contains fragment  “profile_frag”
    b.    Trainings Created:- this tab contains fragment “reg_train”
    c.    Trainings Registered:- this tab contains fragment “reg_train1”
    

## 8. register:-
this activity is for user registration

## 9. SlidingTabLayout and SlidingTabStrip:-
both of these classes are used  to create tab layout in MainActivity 

## 10. training_detail:-
when a user clicks on a training in any recycler view, then this activity gets displayed
that displays all information of the training item being clicked

## 11. URLs:-
it contains all urls to server

## 12. viewHolder:-
this class is used as a viewholder by recyclerviews in MainActivity and Main2Activity

## fragments:-
profile_frag , reg_train , reg_train1 

# Screenshots:-
<div>
<img src="/Screenshots/1.png" alt="Drawing"  height="300" width="180" hspace="20">
<img src="/Screenshots/2.png" alt="Drawing"  height="300" width="180" hspace="20">
<img src="/Screenshots/3.png" alt="Drawing"  height="300" width="180" hspace="20">
<br/><br/>
<img src="/Screenshots/4.png" alt="Drawing"  height="300" width="180" hspace="20">
<img src="/Screenshots/5.png" alt="Drawing"  height="300" width="180" hspace="20">
<br/><br/>
</div>
