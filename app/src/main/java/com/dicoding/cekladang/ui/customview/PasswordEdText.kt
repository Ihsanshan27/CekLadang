package com.dicoding.cekladang.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.cekladang.R

class PasswordEdText : AppCompatEditText {
    private lateinit var iconFormInput: Drawable
    private var characterLength = 0
    private lateinit var eyeOpen: Drawable
    private lateinit var eyeClosed: Drawable
    private var isPasswordVisible = false // Menyimpan status visibilitas password

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        iconFormInput =
            ContextCompat.getDrawable(context, R.drawable.baseline_lock_outline_24) as Drawable
        eyeOpen = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24) as Drawable
        eyeClosed =
            ContextCompat.getDrawable(context, R.drawable.baseline_visibility_off_24) as Drawable
        showIconFormInput()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                characterLength = s.length
                if (s.isNotEmpty() && characterLength < 8) {
                    error = context.getString(R.string.password_short)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }
        })

        setOnTouchListener { _, event ->
            val drawableEnd = compoundDrawables[2]
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.x >= (width - paddingRight - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    performClick() // Panggil performClick untuk memastikan aksesibilitas
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun showIconFormInput() {
        setButtonDrawable(startOfTheText = iconFormInput)
        updateEyeIcon()
    }

    private fun updateEyeIcon() {
        val eyeIcon = if (isPasswordVisible) eyeOpen else eyeClosed
        setButtonDrawable(startOfTheText = iconFormInput, endOfTheText = eyeIcon)
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        transformationMethod = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        text?.let { setSelection(it.length) } // Pastikan kursor di akhir
        updateEyeIcon()
    }


    private fun setButtonDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        context.apply {
            setTextColor(ContextCompat.getColor(this, R.color.black))
            hint = context.getString(R.string.hint_password)
            textSize = 16f
            setHintTextColor(ContextCompat.getColor(this, R.color.gray_800))
            background = ContextCompat.getDrawable(this, R.drawable.form_input)
        }
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
//        transformationMethod = PasswordTransformationMethod.getInstance()
    }
}