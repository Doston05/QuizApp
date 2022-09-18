package com.myapps.quizapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_quiz_questions.*
import kotlinx.android.synthetic.main.activity_quiz_questions.view.*
import kotlinx.android.synthetic.main.activity_result.*

class QuizQuestions : AppCompatActivity(),View.OnClickListener {
   private var mCurrentPosition = 1
    private var mQuestionsList: ArrayList<Questions>? = null
    private var mSelectedOptionPosition: Int = 0
    private var  mCounter:Int=1
    private var mCorrectAnswers:Int=0
    private val TAG = "MainActivity"
    private var totalCoins = 0

    private lateinit var rewardedAd: RewardedAd
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var mUserName:String?=null
    private var flag=true
    private  var index:ArrayList<Int> =ArrayList()
    private  var levelCheck= arrayOf(true,false,false,false,false,false,false,false,false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = applicationContext.getSharedPreferences("MyPref", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()

        mUserName=intent.getStringExtra(Constants.USER_NAME)
        setContentView(R.layout.activity_quiz_questions)
        loadData()
        selectLevel()
        MobileAds.initialize(this)

        rewardedAd()




        mQuestionsList = Constants.getQuestions()
        defaultOptionView()


    }
    private fun rewardedAd() {

        fun createAndLoadRewardedAd(): RewardedAd {
            rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
            val adLoadCallback = object: RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    // Ad successfully loaded.
                    Log.d(TAG,"Rewarded Ad loaded")

                }
                override fun onRewardedAdFailedToLoad(errorCode: Int) {
                    // Ad failed to load.
                    Log.d(TAG,"Rewarded Ad failed to load")
                }
            }
            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
            return rewardedAd
        }
        fun onRewardedAdClosed() {
            this.rewardedAd = createAndLoadRewardedAd()
        }
        createAndLoadRewardedAd()

        button3.setOnClickListener {
            Log.d(TAG,"Button clicked")

            if (rewardedAd.isLoaded) {
                val activityContext: Activity = this@QuizQuestions
                val adCallback = object: RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                        // Ad opened.
                        Log.d(TAG,"Rewarded Ad opened")
                    }
                    override fun onRewardedAdClosed() {
                        // Ad closed.
                        Log.d(TAG,"Rewarded Ad closed")
                        onRewardedAdClosed()
                    }
                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                        // User earned reward.
                        Log.d(TAG,"Reward earned successfully")
                        totalCoins=coins.text.toString().toInt()
                        totalCoins+=5
                        coins.text=totalCoins.toString()

                        Toast.makeText(applicationContext, "5 coins successfully earned", Toast.LENGTH_SHORT).show()
                    }
                    override fun onRewardedAdFailedToShow(errorCode: Int) {
                        Log.d(TAG,"Rewarded Ad failed to show")
                        // Ad failed to display.
                    }
                }
                rewardedAd.show(activityContext, adCallback)
            }
            else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
            }
        }
    }

    private fun setQuestion() {
        defaultOptionView()
        flag=true
        if (mCounter==15){
            submit_btn.text="FINISH"
        }
        else{
            submit_btn.text="SUBMIT"
        }
        val questions: Questions = mQuestionsList!![index[mCounter-1]]
        progressBar.max=15
        progressBar.progress = mCounter
        progress_text.text = "$mCounter" + "/" + progressBar.max
        question_text.text = questions.questions
        question_image.setImageResource(questions.image)
        YoYo.with(Techniques.ZoomIn)
            .duration(1000).repeat(0)
            .playOn(question_image)
        option_one.text = questions.optionOne
        option_one.setShadowLayer(0f,0f, 0F,0)
        option_two.text = questions.optionTwo
        option_three.text = questions.optionThree
        option_four.text = questions.optionFour
        isVisible()
    }
private fun isVisible(){
    option_one.visibility = if (option_one.visibility == View.INVISIBLE){
        View.VISIBLE
    } else{
        View.VISIBLE
    }
    option_two.visibility = if (option_two.visibility == View.INVISIBLE){
        View.VISIBLE
    } else{
        View.VISIBLE
    }
    option_three.visibility = if (option_three.visibility == View.INVISIBLE){
        View.VISIBLE
    } else{
        View.VISIBLE
    }
    option_four.visibility = if (option_four.visibility == View.INVISIBLE){
        View.VISIBLE
    } else{
        View.VISIBLE
    }
}
    private fun defaultOptionView() {
        val options = ArrayList<TextView>()
        options.add(0, option_one)
        options.add(1, option_two)
        options.add(2, option_three)

        options.add(3, option_four)

        for (option in options) {
            option.background = ContextCompat.getDrawable(this, R.drawable.button_gray)

        }
    }

    private fun selectedOptionView(tv: TextView, SelectedOptionPosition: Int) {
        defaultOptionView()
        mSelectedOptionPosition = SelectedOptionPosition
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.button111)

    }


    override fun onClick(v: View?) {
        YoYo.with(Techniques.Pulse)
            .duration(200).repeat(0)
            .playOn(v)
        when(v?.id){
            R.id.option_one->{
                selectedOptionView(option_one,1)
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(v.option_one)
            }
            R.id.option_two->{
                selectedOptionView(option_two,2)
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(v.option_two)
            }
            R.id.option_three->{
                selectedOptionView(option_three,3)
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(v.option_three)
            }
            R.id.option_four->{
                selectedOptionView(option_four,4)
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(v.option_four)
            }
            R.id.button_answer-> {
                if (coins.text.toString().toInt() >= 5) {
                    val questions = mQuestionsList!![index[mCounter - 1]]
                    var coin:Int=coins.text.toString().toInt()
                    coin-=5
                    trueAnswer(questions.correctAnswer, R.drawable.button_green)
                    mSelectedOptionPosition = 0
                    mCounter++
                    mCorrectAnswers++
                    Handler().postDelayed({

                        setQuestion()
                        coins.text= coin.toString()
                    }, 1000)
                }
                else{
                    YoYo.with(Techniques.Shake).duration(200).repeat(0).playOn(button_answer)}
            }
            R.id.button_50_50-> {
                if (coins.text.toString().toInt()>=3&&flag) {
                    val questions = mQuestionsList!![index[mCounter - 1]]
                    (0..4).random()
                    var coin:Int=coins.text.toString().toInt()
                    flag=false
                    coin-=3
                    coins.text=coin.toString()
                    if (1 == questions.correctAnswer) {
                        option_two.visibility = if (option_two.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                        option_three.visibility = if (option_three.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                    }
                    if (2 == questions.correctAnswer) {
                        option_one.visibility = if (option_one.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                        option_four.visibility = if (option_four.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                    }

                    if (3 == questions.correctAnswer) {
                        option_two.visibility = if (option_two.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                        option_one.visibility = if (option_one.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                    }

                    if (4 == questions.correctAnswer) {
                        option_two.visibility = if (option_two.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                        option_three.visibility = if (option_three.visibility == View.VISIBLE) {
                            View.INVISIBLE
                        } else {
                            View.INVISIBLE
                        }
                    }
                }
                else{
                    YoYo.with(Techniques.Shake).duration(200).repeat(0).playOn(button_50_50)}



            }
            R.id.submit_btn->{
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(v.submit_btn)
                    when {
                        mCounter < 15 -> {
                            if (mSelectedOptionPosition == 0){
                            } else {
                                val questions = mQuestionsList?.get(index[mCounter - 1])
                                if (questions!!.correctAnswer != mSelectedOptionPosition) {
                                    trueAnswer(mSelectedOptionPosition, R.drawable.button_red)
                                } else {
                                    mCorrectAnswers++
                                }
                                trueAnswer(questions.correctAnswer, R.drawable.button_green)
                            }

                                mSelectedOptionPosition = 0
                            YoYo.with(Techniques.ZoomOut)
                                .duration(1000).repeat(0)
                                .playOn(question_image)
                            mCurrentPosition++
                            mCounter++
                                Handler().postDelayed({

                                    setQuestion()

                                }, 1000)

                            }

                        else -> {
                            mCurrentPosition++
                            showDialog()
                            levelUp()
                            intent.putExtra(Constants.USER_NAME,mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS,mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS,mQuestionsList!!.size)


                        }
                    }




            }
        }
    }

    private fun trueAnswer(answer:Int,drawableView:Int){
        when(answer){
            1->{
                option_one.background=ContextCompat.getDrawable(this,drawableView)
            }
            2->{
                option_two.background=ContextCompat.getDrawable(this,drawableView)
            }
            3->{
                option_three.background=ContextCompat.getDrawable(this,drawableView)
            }
            4->{
                option_four.background=ContextCompat.getDrawable(this,drawableView)
            }
        }
    }
    private fun showDialog(){
       val dialog= Dialog(this)
        dialog.setContentView(R.layout.activity_result)

        val playerName:TextView=dialog.player_name
        playerName.text="$mUserName"
        val playerScore:TextView=dialog.player_score
        if (mCorrectAnswers<3) {
            dialog.lottie.setAnimation(R.raw.game_over)
            dialog.result_text.text=""
        }
        playerScore.text="Your score is $mCorrectAnswers out of $mCounter"
        val close = dialog.findViewById<Button>(R.id.btn_finish)
        close.setOnClickListener {
            startActivity(Intent(this@QuizQuestions,MainActivity::class.java))
            dialog.dismiss()
        }
        val restart = dialog.findViewById<Button>(R.id.btn_restart)
        restart.setOnClickListener {
          if (mCorrectAnswers>=13){
              restart.text="Next Level"
              mCurrentPosition+=10
              mCounter=0
              restartGame()
              dialog.dismiss()
          }
            else{
              restart.text="Restart"
              mCurrentPosition-=15
              mCounter=0
              restartGame()
              dialog.dismiss()
            }
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()


    }

    override fun onStop() {
        super.onStop()
        saveData()
    }
    private fun saveData(){
        if (mCounter!=16){
        editor=sharedPreferences.edit()
        editor.putString("playerName",mUserName)
        editor.putInt("correctAnswer",mCorrectAnswers)
        editor.putInt("currentPosition",mCurrentPosition)
        editor.putInt("selectedOptionPosition",mSelectedOptionPosition)
            var boolean ="true"
            for (i in 1 until levelCheck.size) {
                boolean += "m${levelCheck[i]}"
            }
            editor.putString("boolean",boolean)
          // var indexx:String=""
          // for (i in 0 until index.size) {
          //     indexx += "${index.get(i)}t"
          // }
          // editor.putString("index", indexx)
        }
        else{
            editor.clear()
            var boolean ="true"
            for (i in 1 until levelCheck.size) {
                boolean += "m${levelCheck[i]}"
            }
            editor.putString("boolean",boolean)
        }
        editor.apply()
    }
    private fun loadData(){
       //if (sharedPreferences.contains("playerName")) {
       //    mUserName = sharedPreferences.getString("playerName", null)
       //}
       //if (sharedPreferences.contains("correctAnswer")) {
       //    mCorrectAnswers = sharedPreferences.getInt("correctAnswer", 0)
       //}
       //if (sharedPreferences.contains("currentPosition")) {
       //    mCurrentPosition = sharedPreferences.getInt("currentPosition", 0)
       //}
       //if (sharedPreferences.contains("selectedOptionPosition")) {
       //    mSelectedOptionPosition = sharedPreferences.getInt("selectedOptionPosition", 0)

       //if (sharedPreferences.contains("index")) {
       //    index.clear()
       //    var temp:String? = sharedPreferences.getString("index", null)
       //    var tempList=temp?.split("t")
       //    for (i in 0 until 25){
       //        index.add(tempList?.get(i)!!.toInt())
       //    }
       //}
        if (sharedPreferences.contains("boolean")) {
            val text = sharedPreferences.getString("boolean", null)
            val temp=text!!.split("m")
            for (i in levelCheck.indices)
                levelCheck[i]=temp[i].toBoolean()

        }
    }


    private fun selectLevel() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_menu)
        if (levelCheck[0])dialog.level_1.setImageResource(R.drawable.one)
        if (levelCheck[1])dialog.level_2.setImageResource(R.drawable.two)
        if (levelCheck[2])dialog.level_3.setImageResource(R.drawable.three)
        if (levelCheck[3])dialog.level_4.setImageResource(R.drawable.four)
        if (levelCheck[4])dialog.level_5.setImageResource(R.drawable.five)
        if (levelCheck[5])dialog.level_6.setImageResource(R.drawable.six)
        if (levelCheck[6])dialog.level_7.setImageResource(R.drawable.seven)
        if (levelCheck[7])dialog.level_8.setImageResource(R.drawable.eight)
        if (levelCheck[8])dialog.level_9.setImageResource(R.drawable.nine)
        dialog.level_1.setOnClickListener {
            if (!levelCheck[0]) {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_1)
            }

            else {
                mCurrentPosition = 1
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_1)

                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_2.setOnClickListener {
            if (!levelCheck[1])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_2)
            }

            else {
                mCurrentPosition = 25
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_2)
                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_3.setOnClickListener {
            if (!levelCheck[2])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_3)
            }

            else {
                mCurrentPosition = 50
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_3)
                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_4.setOnClickListener {
            if (!levelCheck[3])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_4)
            }

            else {
                mCurrentPosition = 75
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_4)
                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_5.setOnClickListener {
            if (!levelCheck[4])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_5)
            }

            else {
                mCurrentPosition = 100
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_5)

                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_6.setOnClickListener {
            if (!levelCheck[5])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_6)
            }

            else {
                mCurrentPosition = 125
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_1)
                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_7.setOnClickListener {
            if (!levelCheck[6])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_7)
            }

            else {
                mCurrentPosition = 150
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_1)
                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_8.setOnClickListener {
            if (!levelCheck[7])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_8)
            }

            else {
                mCurrentPosition = 175
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_1)
                for (i in mCurrentPosition..mCurrentPosition+25){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.level_9.setOnClickListener {
            if (!levelCheck[8])
               {
                Toast.makeText(
                    this,
                    "To unlock this level you should complete previous levels",
                    Toast.LENGTH_SHORT
                ).show()
                YoYo.with(Techniques.Shake)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_9)
            }

            else {
                mCurrentPosition = 200
                mSelectedOptionPosition = 0
                mCounter=1
                YoYo.with(Techniques.Pulse)
                    .duration(200).repeat(0)
                    .playOn(dialog.level_1)
                for (i in mCurrentPosition..mCurrentPosition+23){
                    index.add(i)
                }
                index.shuffle()
                restartGame()
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
private fun levelUp() {
if (mCorrectAnswers>=3&& mCurrentPosition==15){
    levelCheck[1]=true

}
    if (mCorrectAnswers>=3&& mCurrentPosition==40){
        levelCheck[2]=true


    }
    if (mCorrectAnswers>=3&& mCurrentPosition==65){
        levelCheck[3]=true


    }
    if (mCorrectAnswers>=3&& mCurrentPosition==90){
        levelCheck[4]=true


    }
    if (mCorrectAnswers>=3&& mCurrentPosition==115){
        levelCheck[5]=true


    }
    if (mCorrectAnswers>=3&& mCurrentPosition==140){
        levelCheck[6]=true


    }
    if (mCorrectAnswers>=3&& mCurrentPosition==165){
        levelCheck[7]=true


    }
    if (mCorrectAnswers>=3&& mCurrentPosition==190){
        levelCheck[8]=true


    }

}

    private fun restartGame(){
        mSelectedOptionPosition=1
        mCorrectAnswers=0
        mCounter=1
        index.shuffle()
        setQuestion()
        defaultOptionView()



    }
}



