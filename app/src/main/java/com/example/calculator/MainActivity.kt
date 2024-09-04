package com.example.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private var lastNumeric: Boolean = false
    private var stateError: Boolean = false
    private var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)
    }

    fun onDigit(view: View) {
        if (stateError) {
            tvResult.text = (view as Button).text
            stateError = false
        } else {
            tvResult.append((view as Button).text)
        }
        lastNumeric = true
    }

    fun onOperator(view: View) {
        if (lastNumeric && !stateError) {
            tvResult.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    fun onClear(view: View) {
        tvResult.text = ""
        lastNumeric = false
        stateError = false
        lastDot = false
    }

    fun onEqual(view: View) {
        if (lastNumeric && !stateError) {
            val input = tvResult.text.toString()
            try {
                val result = evaluate(input)
                tvResult.text = result.toString()
                lastDot = true
            } catch (e: IllegalArgumentException) {
                tvResult.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

    fun onDecimalPoint(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            tvResult.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    private fun evaluate(expression: String): Double {
        return try {
            val rhino = org.mozilla.javascript.Context.enter()
            rhino.optimizationLevel = -1
            val scriptable = rhino.initStandardObjects()
            val result = rhino.evaluateString(scriptable, expression, "JavaScript", 1, null)

            result.toString().toDouble()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid expression")
        } finally {
            org.mozilla.javascript.Context.exit()
        }
    }
}
