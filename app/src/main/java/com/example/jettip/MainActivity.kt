package com.example.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettip.components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.util.calculateTotalPerPerson
import com.example.jettip.util.calculateTotalTip
import com.example.jettip.widget.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp {
//                MyHeader()
                MainContext()

            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Surface {
            content()

        }
    }
}


@Composable
fun MyHeader(totalPerPerson: Double = 0.0) {
    Surface(
        color = Color(0xFFE9D7F7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person", style = MaterialTheme.typography.titleSmall)
            Text(
                text = "$$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

    }
}


@Composable
fun MainContext() {
    val tipAmountState = remember {
        mutableDoubleStateOf(
            0.0
        )
    }
    val totalSplitPerPersonState = remember {
        mutableDoubleStateOf(
            0.0
        )
    }
    val splitByState = remember {
        mutableIntStateOf(
            1
        )
    }
    val range = IntRange(
        start = 1,
        endInclusive = 100
    )
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(
                all = 10.dp
            )
    ) {

        BillForm (
            tipAmountState = tipAmountState,
            totalSplitPerPersonState = totalSplitPerPersonState,
            splitByState = splitByState,
            range = range
        )
    }
}

@Composable
fun BillForm(modifier: Modifier = Modifier,
             range: IntRange = 1..100,
             splitByState: MutableIntState,
             tipAmountState: MutableDoubleState,
             totalSplitPerPersonState: MutableDoubleState,
             onValChange: (String) -> Unit = {}) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableFloatStateOf(
            0f
        )
    }



    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()



    Column {
        MyHeader(
            totalPerPerson = totalSplitPerPersonState.doubleValue
        )
        Surface(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(), shape = RoundedCornerShape(
                corner = CornerSize(8.dp),

                ), border = BorderStroke(width = 1.dp, color = Color.LightGray)

        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())


                        keyboardController?.hide()

                    },


                    )

                if (
                                validState

                ) {
                    Row(

                        modifier = Modifier.padding(3.dp)
                    ) {
                        Text(
                            "Split",
                            textAlign = TextAlign.Center,
                        )
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {
                                    splitByState.intValue =
                                        if (splitByState.intValue > 1) splitByState.intValue - 1 else 1
                                    totalSplitPerPersonState.doubleValue = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage = tipPercentage
                                    )
                                }
                            )
                            Text(splitByState.intValue.toString(), textAlign = TextAlign.Center)
                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    if (splitByState.intValue < range.last)
                                        splitByState.intValue += 1
                                    totalSplitPerPersonState.doubleValue = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage = tipPercentage
                                    )
                                }
                            )
                        }


                    }

                    Row {
                        Text(
                            "Tip", modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                        Spacer(
                            modifier = Modifier.width(200.dp)
                        )
                        Text(
                            "$ ${tipAmountState.doubleValue}", modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$tipPercentage%")
                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Slider(
                            value = sliderPositionState.floatValue,
                            steps = 5,
                            modifier = Modifier.padding(
                                start = 16.dp, end = 16.dp
                            ),
                            onValueChange = { newVal ->
                                sliderPositionState.floatValue = newVal

                                tipAmountState.doubleValue = calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )
                                totalSplitPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.intValue,
                                    tipPercentage = tipPercentage
                                )

                            }
                        )

                    }
                } else {
                    Box() {}
                }
            }
        }
    }


}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipTheme {
        MyApp {
            MainContext()

        }
    }
}