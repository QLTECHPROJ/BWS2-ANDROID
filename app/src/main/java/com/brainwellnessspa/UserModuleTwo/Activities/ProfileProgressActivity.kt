package com.brainwellnessspa.UserModuleTwo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DassAssSliderTwo.Activity.AssProcessActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityProfileProgressBinding

class ProfileProgressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileProgressBinding
    var qusOne: String = ""
    var qusTwo: String = ""
    var qusThree: String = ""
    var qusFour: String = ""
    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_progress)
        ctx = this@ProfileProgressActivity
        binding.llFirst.visibility = View.VISIBLE
        binding.llSecond.visibility = View.GONE
        binding.llThird.visibility = View.GONE
        binding.llForth.visibility = View.GONE
        binding.llFifth.visibility = View.GONE
        binding.btnMySelf.setOnClickListener {
            qusOne = "Myself";
            binding.btnMySelf.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnMySelf.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOthers.setTextColor(resources.getColor(R.color.black))
            binding.btnOthers.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnOthers.setOnClickListener {
            qusOne = "Others";
             binding.btnMySelf.setTextColor(resources.getColor(R.color.black))
             binding.btnMySelf.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
             binding.btnOthers.setTextColor(resources.getColor(R.color.light_blue_theme))
             binding.btnOthers.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        }
        binding.btnFirstDone.setOnClickListener {
            if(qusOne.isEmpty() || qusOne.equals("")){
                BWSApplication.showToast("Please select Ans",ctx)
            }else {
                binding.llIndicate.progress = 1
                binding.llFirst.visibility = View.GONE
                binding.llSecond.visibility = View.VISIBLE
                binding.llThird.visibility = View.GONE
                binding.llForth.visibility = View.GONE
                binding.llFifth.visibility = View.GONE
            }
        }
        binding.btnMale.setOnClickListener {
            qusTwo = "Male";
            binding.btnMale.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnMale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemale.setTextColor(resources.getColor(R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(resources.getColor(R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnFemale.setOnClickListener {
            qusTwo = "Female";
            binding.btnMale.setTextColor(resources.getColor(R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnFemale.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnGenX.setTextColor(resources.getColor(R.color.black))
            binding.btnGenX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnGenX.setOnClickListener {
            qusTwo = "Gender X";
            binding.btnMale.setTextColor(resources.getColor(R.color.black))
            binding.btnMale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemale.setTextColor(resources.getColor(R.color.black))
            binding.btnFemale.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnGenX.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnGenX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        }
        binding.btnSecondDone.setOnClickListener {
            if(qusTwo.isEmpty() || qusTwo.equals("")){
                BWSApplication.showToast("Please select Ans",ctx)
            }else {
                if(qusTwo.equals("Gender X")) {
                    binding.llFirst.visibility = View.GONE
                    binding.llSecond.visibility = View.GONE
                    binding.llThird.visibility = View.VISIBLE
                    binding.llForth.visibility = View.GONE
                    binding.llFifth.visibility = View.GONE
                }else{
                    binding.llIndicate.progress = 2
                    binding.llFirst.visibility = View.GONE
                    binding.llSecond.visibility = View.GONE
                    binding.llThird.visibility = View.GONE
                    binding.llForth.visibility = View.VISIBLE
                    binding.llFifth.visibility = View.GONE
                }
            }
        }
        binding.btnMaleGX.setOnClickListener {
            qusTwo = "Male";
            binding.btnMaleGX.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(resources.getColor(R.color.black))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnFemaleGX.setOnClickListener {
            qusTwo = "Female";
            binding.btnMaleGX.setTextColor(resources.getColor(R.color.black))
            binding.btnMaleGX.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnFemaleGX.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnFemaleGX.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        }
        binding.btnThirdDone.setOnClickListener {
            if(qusTwo.isEmpty() || qusTwo.equals("")){
                BWSApplication.showToast("Please select Ans",ctx)
            }else {
                  binding.llIndicate.progress = 2
                  binding.llFirst.visibility = View.GONE
                  binding.llSecond.visibility = View.GONE
                  binding.llThird.visibility = View.GONE
                  binding.llForth.visibility = View.VISIBLE
                  binding.llFifth.visibility = View.GONE
              }
        }
        binding.btnOpn1.setOnClickListener {
            qusThree = "0 - 4";
            binding.btnOpn1.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn2.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnOpn2.setOnClickListener {
            qusThree = "5 -12";
            binding.btnOpn1.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn3.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnOpn3.setOnClickListener {
            qusThree = "13 - 17";
            binding.btnOpn1.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnOpn4.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnOpn4.setOnClickListener {
            qusThree = "> 18";
            binding.btnOpn1.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn1.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn2.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn2.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn3.setTextColor(resources.getColor(R.color.black))
            binding.btnOpn3.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnOpn4.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnOpn4.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        }
        binding.btnForthDone.setOnClickListener {
            if (qusThree.isEmpty() || qusThree.equals("")) {
                BWSApplication.showToast("Please select Ans", ctx)
            } else {
                binding.llIndicate.progress = 3
                binding.llFirst.visibility = View.GONE
                binding.llSecond.visibility = View.GONE
                binding.llThird.visibility = View.GONE
                binding.llForth.visibility = View.GONE
                binding.llFifth.visibility = View.VISIBLE
            }
        }
        binding.btnYes.setOnClickListener {
            qusFour = "Yes";
            binding.btnYes.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnYes.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
            binding.btnNo.setTextColor(resources.getColor(R.color.black))
            binding.btnNo.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
        }
        binding.btnNo.setOnClickListener {
            qusFour = "No";
            binding.btnYes.setTextColor(resources.getColor(R.color.black))
            binding.btnYes.setBackgroundResource(R.drawable.light_gray_rounded_unfilled)
            binding.btnNo.setTextColor(resources.getColor(R.color.light_blue_theme))
            binding.btnNo.setBackgroundResource(R.drawable.light_blue_rounded_unfilled)
        }
        binding.btnFifthDone.setOnClickListener {
            if (qusFour.isEmpty() || qusFour.equals("")) {
                BWSApplication.showToast("Please select Ans", ctx)
            } else {
                binding.llIndicate.progress = 4
                binding.llFirst.visibility = View.GONE
                binding.llSecond.visibility = View.GONE
                binding.llThird.visibility = View.GONE
                binding.llForth.visibility = View.GONE
                binding.llFifth.visibility = View.VISIBLE
                val i = Intent(this@ProfileProgressActivity, AssProcessActivity::class.java)
                i.putExtra(CONSTANTS.ASSPROCESS, "0")
                startActivity(i)
                finish()
            }
            }
        }
}