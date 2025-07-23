package org.prosoljava.assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


/**
 * 계산기 Class
 *
 * @author 김민우
 * @version 1.0
 * @since 2024-11-25~2024-12-01
 */
public class CalculatingMachine extends JFrame implements ActionListener {
    private JTextField display;
    private String nowInput = "";
    private double result = 0;
    private String operator = "";//마지막으로 사용한 연산자
    //새로운 숫자체크용 변수
    private boolean startNewNumber = true;
    //메모리 관리용 class
    MemoryManager mM = new MemoryManager();
    private Map<String, JButton> buttonMap = new HashMap<>(); // 버튼 저장용 Map
    private Map<String, JButton> UnaryOperators  = new HashMap<>(); // 버튼(단항연산자) 저장용 Map
    private Map<String, JButton> PiE  = new HashMap<>(); // 버튼(Pi,E) 저장용 Map
    private Map<String, JButton> Oper  = new HashMap<>(); // 버튼(+-*/) 저장용 Map

    //두번째 입력을 체크하는 변수
    //ex) 1입력 +입력 숫자입력시 true
    private boolean secondNumber = false;
    
    //아래 2개의 변수는 키보드 이벤트에서 기존 버튼을 비활성화하는 방법을 대체하기 위한 확인용 변수
    private boolean operBtn_Enabled = true;
    private boolean zeroBtn_Enabled = true;


    /**
     * 계산기 생성자
     */
    public CalculatingMachine() {
        //계산기 설정
        setTitle("Calculating Machine");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //키보드 이벤트 Class
        MyKeyListener keyEve = new MyKeyListener();
        addKeyListener(keyEve);

        //계산기 수식 텍스트
        display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.addKeyListener(keyEve);
        add(display, BorderLayout.NORTH);

        //버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 5, 5, 5));
        buttonPanel.addKeyListener(keyEve);

        // 버튼 배열설정
        String[] buttons = {
                "MC", "MR", "M+", "%", "C",
                "7", "8", "9", "/", "√x",
                "4", "5", "6", "*", "x^2",
                "1", "2", "3", "-", "n!",
                "0", ".", "=", "+", "logx",
                "pi", "e", "lnx", "e^x", "10^x",
                "sin", "cos", "tan","",""
        };

        //단항연산자 체크용 배열
        String[] UObtn = new String[]{"%","√x", "x^2", "n!", "logx", "lnx", "e^x", "10^x", "cos", "tan", "sin"};

        //연산자 체크용
        String[] Operbtn = new String[]{"/","*","-","+"};

        //버튼 배치
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.addActionListener(this);
            buttonPanel.add(button);
            button.addKeyListener(keyEve);

            //버튼을 가져오오기 위한 변수 설정
            //해당 변수를 통해 특정버튼을 활성화 비활성화한다.
            buttonMap.put(text, button);

            //단한연산자 전체를 비활성화하기 위해 변수세팅
            if(Arrays.asList(UObtn).contains(text))
                UnaryOperators.put(text, button);
            //특수한 상수(pi,e)전체를 비활성화하기 위해 변수세팅
            if(text.equals("pi")||text.equals("e"))
                PiE.put(text, button);

            if(Arrays.asList(Operbtn).contains(text))
                Oper.put(text, button);
        }

        add(buttonPanel, BorderLayout.CENTER);

    }

    /**
     * MyKeyListener
     * 키보드 이벤트 클래스
     *
     * 0~9,enter(=),-+/*,C(clear)지원
     *
     */
    class MyKeyListener extends KeyAdapter {
        /**
         * keyPressed
         * 키보드를 눌렀을 때 발생하는 이벤트
         *
         * @param e 발생한 키보드이벤트에 대한 이벤트 정보
         */
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            String command="";
            
            //코드확인용
            //System.out.println(keyCode);
            switch(keyCode) {
                case KeyEvent.VK_0://int값 :48
                    if(!zeroBtn_Enabled)    return;//0버튼이 비활성화시 실행 x
                case KeyEvent.VK_1:
                case KeyEvent.VK_2:
                case KeyEvent.VK_3:
                case KeyEvent.VK_4:
                case KeyEvent.VK_5:
                case KeyEvent.VK_6:
                case KeyEvent.VK_7:
                case KeyEvent.VK_8:
                case KeyEvent.VK_9:
                    command = ""+(keyCode-48);
                    break;
                case KeyEvent.VK_NUMPAD0://int값 :96
                    if(!zeroBtn_Enabled)    return;//0버튼이 비활성화시 실행 x
                case KeyEvent.VK_NUMPAD1:
                case KeyEvent.VK_NUMPAD2:
                case KeyEvent.VK_NUMPAD3:
                case KeyEvent.VK_NUMPAD4:
                case KeyEvent.VK_NUMPAD5:
                case KeyEvent.VK_NUMPAD6:
                case KeyEvent.VK_NUMPAD7:
                case KeyEvent.VK_NUMPAD8:
                case KeyEvent.VK_NUMPAD9:
                    command = ""+(keyCode-96);
                    break;
                case KeyEvent.VK_ADD:
                    if(!operBtn_Enabled) return;
                    command = "+";
                    break;
                case KeyEvent.VK_SUBTRACT:
                    if(!operBtn_Enabled) return;
                    command = "-";
                    break;
                case KeyEvent.VK_DIVIDE:
                    if(!operBtn_Enabled) return;
                    command = "/";
                    break;
                case KeyEvent.VK_MULTIPLY:
                    if(!operBtn_Enabled) return;
                    command = "*";
                    break;
                case 10:
                    command = "=";
                    break;
                case 67://c를 누르면 클리어
                    command = "C";
                    break;
                    default:
                        keyCode=-1;
            }
            //관리 외에 코드는 실행x
            if(keyCode!=-1)
                mainFunction(command);
        }
    }

    /**
     * setUnaryOperatorsEnable
     * 단항연산의 버튼을 비활성화한다.
     * -> 연산중일 때 단항연산버튼을 누르면 오류의 여지가 커서 비활성화한다.
     *
     * @param bool 활성화결정변수
     */
    public void setUnaryOperatorsEnable(boolean bool) {
        for (JButton button : UnaryOperators.values())
            button.setEnabled(bool); // 버튼 활성화
    }

    /**
     * setOperEnable
     * 기본적인 연산의 버튼을 비활성화한다.
     * -> 음수를 입력할 때 "-"를 클릭하면 비활성화한다.
     *
     * @param bool 활성화결정변수
     */
    public void setOperEnable(boolean bool) {
        for (JButton button : Oper.values())
            button.setEnabled(bool); // 버튼 활성화
    }

    /**
     * setPiEEnable
     * Pi E의 버튼을 비활성화한다.
     * -> 숫자를 입력한 후에 pi e버튼을 막아 의도치않은 결과를 막는다.
     *
     * @param bool 활성화결정변수
     */
    public void setPiEEnable(boolean bool) {
        for (JButton button : PiE.values())
            button.setEnabled(bool); // 버튼 활성화
    }


    /**
     * actionPerformed
     * 버튼을 클릭했을 때 발생
     *
     * @param e 이벤트에 대한 정보가 담겨있다.
     */
    public void actionPerformed(ActionEvent e) {
        mainFunction(e.getActionCommand());
    }

    /**
     * mainFunction
     * actionPerformed,keyPressed 이벤트에서 활성화한 코드를 기준으로 실행된다.
     *
     * @param command 어떤것을 실행할지 구분할 수 있는 String변수
     */
    private void mainFunction(String command){


        JButton zeroButton = buttonMap.get("0");
        if (zeroButton != null) {
            zeroButton.setEnabled(true); // 버튼 활성화
            zeroBtn_Enabled = true;
        }
        setOperEnable(true);
        operBtn_Enabled = true;

        if (command.matches("[0-9]")) {
            // 숫자 버튼
            if (startNewNumber) {
                nowInput = command;
                startNewNumber = false;
                if(secondNumber){
                    setPiEEnable(false);
                }
            } else {
                nowInput += command;
            }
            display.setText(nowInput);
        } else if (command.equals(".")) {
            // 소수점
            if (!nowInput.contains(".")) {
                nowInput += ".";
            }
            display.setText(nowInput);
        } else if (command.equals("C")) {
            // 초기화
            nowInput = "";
            operator = "";
            result = 0;
            startNewNumber = true;
            secondNumber = false;
            display.setText("");
        } else if (command.equals("=")) {
            //현재까지 입력된 값들 연산처리
            calculate(Double.parseDouble(nowInput));
            operator = "";
            display.setText(String.valueOf(result));
            nowInput = String.valueOf(result);
            startNewNumber = true;
            secondNumber=false;
            setPiEEnable(true);
        } else if (command.matches("[+*/]")) {
            if(isEmptyCheck()) {
                display.setText("Error");
            }else {
                setPiEEnable(true);
                if (command.equals("/")) {
                    zeroButton = buttonMap.get("0"); // "0" 버튼 가져오기
                    zeroButton.setEnabled(false); // 버튼 비활성화
                    zeroBtn_Enabled = false;
                }
                // 연산자 처리
                if (!operator.isEmpty() && !startNewNumber) {
                    calculate(Double.parseDouble(nowInput));
                    display.setText(String.valueOf(result));
                    nowInput = String.valueOf(result);
                }else {
                    result = Double.parseDouble(nowInput);
                }
                operator = command;
                startNewNumber = true;
                secondNumber = true;
            }
        } else if (command.equals("-")) {//빼기 또는 음수 처리
            //음수처리
            if (startNewNumber && nowInput.isEmpty()) {
                nowInput = "-";
                display.setText(nowInput);
                startNewNumber = false;
                setOperEnable(false);
                operBtn_Enabled = false;
                secondNumber=true;
            } else {//빼기처리
                if (!operator.isEmpty() && !startNewNumber) {
                    calculate(Double.parseDouble(nowInput));
                    display.setText(String.valueOf(result));
                    nowInput = String.valueOf(result);
                } else {
                    result = Double.parseDouble(nowInput);
                }
                operator = command;
                startNewNumber = true;
                secondNumber=true;
            }
        } else if (command.equals("√x")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else{
                //루트
                double value = Double.parseDouble(nowInput);
                value = Math.sqrt(value);
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber=false;
            }
        } else if (command.equals("x^2")) {
            if(isEmptyCheck()){
                display.setText("Error");

            }else {
                //제곱
                double value = Double.parseDouble(nowInput);
                value = value * value;
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber = false;
            }
        } else if (command.equals("n!")) {
            if(isEmptyCheck()){
                display.setText("Error");

            }else {
                //팩토리얼
                double value = Double.parseDouble(nowInput);
                value = factFun(value);//팩토리얼 계산 함수
                if (value == Double.NaN)
                    display.setText("Error");
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber = false;
            }
        } else if (command.equals("logx")) {
            if(isEmptyCheck()){
                display.setText("Error");

            }else {
                // 로그
                double value = Double.parseDouble(nowInput);
                value = Math.log10(value);
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber = false;
            }
        } else if (command.equals("lnx")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else {
                // 자연로그
                double value = Double.parseDouble(nowInput);
                value = Math.log(value);
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber=false;
            }
        } else if (command.equals("e^x")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else {
                // 지수 함수
                double value = Double.parseDouble(nowInput);
                value = Math.exp(value);
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber = false;
            }
        } else if (command.equals("10^x")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else {
                // 10의 거듭제곱
                double value = Double.parseDouble(nowInput);
                value = Math.pow(10, value);
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber=false;
            }
        } else if (command.equals("pi")) {
            // 파이 값 입력
            nowInput = String.valueOf(Math.PI);
            display.setText(nowInput);
            startNewNumber = true;
            secondNumber=false;
            if(operator.matches("[+*/]")){
                calculate(Double.parseDouble(nowInput));
                operator = "";
                display.setText(String.valueOf(result));
                nowInput = String.valueOf(result);
                startNewNumber = true;
                secondNumber=false;
            }
        } else if (command.equals("e")) {
            //e 입력
            nowInput = String.valueOf(Math.E);
            display.setText(nowInput);
            startNewNumber = true;
            secondNumber=false;
            if(operator.matches("[+*/]")){
                calculate(Double.parseDouble(nowInput));
                operator = "";
                display.setText(String.valueOf(result));
                nowInput = String.valueOf(result);
                startNewNumber = true;
                secondNumber=false;
            }
        } else if (command.equals("MC")) {//메모리에 값을 초기화
            mM.clearMemory();
        } else if (command.equals("MR")) {//메모리에 값을 읽는다.
            nowInput = String.valueOf(mM.recallMemory());
            display.setText(nowInput);
            startNewNumber = true;
        } else if (command.equals("M+")) {
            if(isEmptyCheck()) {
                display.setText("Error");
            }
            else{
                // 메모리 추가
                // 메모리 값+ 추가된 값
                mM.addToMemory(Double.parseDouble(nowInput));
            }
        }else if (command.equals("%")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else {
                //백분율
                //해당 연산자도 오류를 줄이기위하여 단일연산자 처리하였다.
                double value = Double.parseDouble(nowInput);
                value = value / 100;//백분율표현

                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber = false;
            }
        }else if (command.equals("cos")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else{
                //cos
                double value = Double.parseDouble(nowInput);
                //라디안처리 후 계산
                value = Math.cos(Math.toRadians(value));
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber=false;
            }
        }else if (command.equals("sin")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else{
                //sin
                double value = Double.parseDouble(nowInput);
                //라디안처리 후 계산
                value = Math.sin(Math.toRadians(value));
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber=false;
            }
        }else if (command.equals("tan")) {
            if(isEmptyCheck()){
                display.setText("Error");
            }else{
                //tan
                double value = Double.parseDouble(nowInput);
                //라디안처리 후 계산
                value = Math.tan(Math.toRadians(value));
                display.setText(String.valueOf(value));
                nowInput = String.valueOf(value);
                startNewNumber = true;
                secondNumber=false;
            }
        }

        //secondNumber와 startNewNumber값을 기준으로 단항연사자들을 비활성화한다.
        //secondNumber : 두번째입력상태
        setUnaryOperatorsEnable(!(secondNumber && !startNewNumber));
    }

    /**
     * calculate
     * 사칙연산을 계산한다.
     *
     * @param number 기존의 값에 연산할 값
     */
    private void calculate( double number) {
        //실수의 경우 가비지값으로인한 값에 오류가 너무 많아 BigDecimal 변수 이용
        BigDecimal a = BigDecimal.valueOf(number),b = BigDecimal.valueOf(result);
        if(operator.equals("+")) b = b.add(a);
        if(operator.equals("-")) b = b.subtract(a);
        if(operator.equals("*")) b = b.multiply(a);
        if(operator.equals("/")) b = b.divide(a, 17,RoundingMode.HALF_UP);//최대 소수점이 17자리 인것같아서 최대로 설정


        //이용 후에는 DOUBLE로 다시 바꿔준다.
        result= b.doubleValue();
    }

    /**
     * factFun
     * 팩토리얼 연산
     *
     * @param num 값을 팩토리얼한다.
     * @return result 연산한 값
     */
    private double factFun(double num) {
        if (num < 0 || num != Math.floor(num)) {
            return Double.NaN;
        }
        double result = 1;
        for (int i = 2; i <= (int) num; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * isEmptyCheck
     * 현재 값이 비어있거나 Error가 난 상태라 처리할 숫자가 없을 때 시도를 할 경우 오류가난다.
     * 그러한 값들을 체크하는 용도
     *
     */
    private boolean isEmptyCheck() {
        return display.getText().isEmpty()||display.getText().equals("Error");
    }

    /**
     * main
     *
     * @param args 파라미터로 넘어온 값, 현재는 넘기지 않는다.
     */
    public static void main(String[] args) {
        CalculatingMachine calculator = new CalculatingMachine();
        calculator.setVisible(true);
    }
}
