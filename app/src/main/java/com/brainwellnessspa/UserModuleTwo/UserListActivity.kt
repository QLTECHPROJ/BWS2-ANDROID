package com.brainwellnessspa.UserModuleTwo

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityUserListBinding
import com.brainwellnessspa.databinding.UserListLayoutBinding

class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding
    var PopUp: String = "0"
    var dummyKEy: String = "0"
    lateinit var adapter: UserListAdapter
    private val userList = ArrayList<UserListModel>()
    private lateinit var editTexts: Array<EditText>
    var tvSendOTPbool: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)

        if (intent.extras != null) {
            PopUp = intent.getStringExtra(CONSTANTS.PopUp).toString()
        }

        if (PopUp.equals("1", ignoreCase = true)) {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.comfirm_pin_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            val btnDone: Button = dialog.findViewById(R.id.btnDone)
            val txtError: TextView = dialog.findViewById(R.id.txtError)
            val edtOTP1: EditText = dialog.findViewById(R.id.edtOTP1)
            val edtOTP2: EditText = dialog.findViewById(R.id.edtOTP2)
            val edtOTP3: EditText = dialog.findViewById(R.id.edtOTP3)
            val edtOTP4: EditText = dialog.findViewById(R.id.edtOTP4)

            /* editTexts = arrayOf<EditText>(edtOTP1, edtOTP2, edtOTP3, edtOTP4)
             edtOTP1.addTextChangedListener(PinTextWatcher(0, edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts), edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts)
             edtOTP2.addTextChangedListener(PinTextWatcher(1), edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts)
             edtOTP3.addTextChangedListener(PinTextWatcher(2), edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts)
             edtOTP4.addTextChangedListener(PinTextWatcher(3), edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts)
             edtOTP1.setOnKeyListener(PinOnKeyListener(0, edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts))
             edtOTP2.setOnKeyListener(PinOnKeyListener(1, edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts))
             edtOTP3.setOnKeyListener(PinOnKeyListener(2, edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts))
             edtOTP4.setOnKeyListener(PinOnKeyListener(3, edtOTP1, edtOTP2, edtOTP3,
                     edtOTP4, txtError, btnDone, tvSendOTPbool, editTexts))*/
            dialog.setOnKeyListener { v: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            btnDone.setOnClickListener {
                if (edtOTP1.text.toString().equals("", ignoreCase = true)
                        && edtOTP2.text.toString().equals("", ignoreCase = true)
                        && edtOTP3.text.toString().equals("", ignoreCase = true)
                        && edtOTP4.text.toString().equals("", ignoreCase = true)) {
                    txtError.text = "Please enter OTP"
                } else {
                    val i = Intent(this@UserListActivity, WalkScreenActivity::class.java)
                    startActivity(i)
                    dialog.dismiss()
                }
            }
            dialog.show()
            dialog.setCancelable(false)
        } else {

        }
        binding.llBack.setOnClickListener {
            finish()
        }

        binding.llAddNewUser.setOnClickListener {
            val i = Intent(this@UserListActivity, AddProfileActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnLogIn.setOnClickListener {
            val i = Intent(this@UserListActivity, WalkScreenActivity::class.java)
            startActivity(i)
        }
        binding.rvUserList.layoutManager = LinearLayoutManager(this@UserListActivity)
        adapter = UserListAdapter(userList)
        binding.rvUserList.adapter = adapter

        prepareUserData();
    }

    private fun prepareUserData() {
        var userAdd = UserListModel("Jhon Smith")
        userList.add(userAdd)
        userAdd = UserListModel("Jhon Smith")
        userList.add(userAdd)
        userAdd = UserListModel("Jhon Smith")
        userList.add(userAdd)
        userAdd = UserListModel("Jhon Smith")
        userList.add(userAdd)

        adapter.notifyDataSetChanged();
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    class UserListAdapter(listModel: List<UserListModel>) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
        private val listModel: List<UserListModel>

        init {
            this.listModel = listModel
        }

        inner class MyViewHolder(bindingAdapter: UserListLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            var bindingAdapter: UserListLayoutBinding

            init {
                this.bindingAdapter = bindingAdapter
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: UserListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvName.text = listModel.get(position).name
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

    /* class PinTextWatcher internal constructor(private val currentIndex: Int, var edtOTP1: EditText, var edtOTP2: EditText,
                                               var edtOTP3: EditText, var edtOTP4: EditText, var txtError: TextView,
                                               var btnDone: Button, var tvSendOTPbool: Boolean, editTexts) : TextWatcher {
         private var isFirst = false
         private var isLast = false
         private var newTypedString = ""

         init {
             if (currentIndex == 0) isFirst = true else if (currentIndex == editTexts.size - 1) isLast = true
         }
         override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
             newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
             val OTP1: String = edtOTP1.getText().toString().trim()
             val OTP2: String = edtOTP2.getText().toString().trim()
             val OTP3: String = edtOTP3.getText().toString().trim()
             val OTP4: String = edtOTP4.getText().toString().trim()
             if (!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty()) {
                 btnDone.setEnabled(true)
                 btnDone.setBackgroundResource(R.drawable.extra_round_cornor)
             } else {
                 btnDone.setEnabled(false)
                 btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
             }
         }

         override fun afterTextChanged(s: Editable) {
             var text = newTypedString
             Log.e("OTP VERIFICATION", "" + text)

             *//* Detect paste event and set first char *//*if (text.length > 1) text = text[0].toString() // TODO: We can fill out other EditTexts
            editTexts.get(currentIndex).removeTextChangedListener(this)
            editTexts.get(currentIndex).setText(text)
            editTexts.get(currentIndex).setSelection(text.length)
            editTexts.get(currentIndex).addTextChangedListener(this)
            if (text.length == 1) {
                moveToNext()
            } else if (text.length == 0) {
                if (!tvSendOTPbool) {
                    editTexts.get(0).requestFocus()
                } else {
                    moveToPrevious()
                }
            }
        }

        private fun moveToNext() {
            if (!isLast) editTexts.get(currentIndex + 1).requestFocus()
            if (isAllEditTextsFilled && isLast) { // isLast is optional
                editTexts.get(currentIndex).clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts.get(currentIndex - 1).requestFocus()
        }

        private val isAllEditTextsFilled: Boolean
            private get() {
                for (editText in editTexts) if (editText.text.toString().trim { it <= ' ' }.length == 0) return false
                return true
            }

        private fun hideKeyboard() {
            if (getCurrentFocus() != null) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0)
            }
        }

    }

    class PinOnKeyListener internal constructor(private val currentIndex: Int) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (editTexts.get(currentIndex).getText().toString().isEmpty() && currentIndex != 0) editTexts.get(currentIndex - 1).requestFocus()
            }
            return false
        }
    }*/
}


