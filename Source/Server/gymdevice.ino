// Title: Gym Training Device
// Author: Ben Royans
// Date: 27/11/2019
// Client: Barry Lawson

#define BUTTON_PIN 10

#define buzzer 6

// 595 board
#define latchPin 9
#define clockPin 8
#define dataPin 7

// Station counter
int station = 1;

// Station time counter
double timer;
bool rest = false;

// Station times
double station1_duration = 5; // 20 Seconds
double station1_rest = 5;     // 10 Seconds

double station2_duration = 5; // 30 Seconds
double station2_rest = 5;     // 30 Seconds

double station3_duration = 5; // 45 Seconds
double station3_rest = 5;     // 15 Seconds

// Flash speed
int flashDelay = 100;

void setup()
{
    // Setup pins
    pinMode(buzzer, OUTPUT);

    pinMode(latchPin, OUTPUT);
    pinMode(clockPin, OUTPUT);
    pinMode(dataPin, OUTPUT);

    // Begin serial output
    Serial.begin(9600);

    // Button
    pinMode(BUTTON_PIN, INPUT);
    digitalWrite(BUTTON_PIN, HIGH); // connect internal pull-up

    // Startup tone
    tone(buzzer, 700, 100);
    delay(50);
    tone(buzzer, 800, 100);
    delay(50);
    tone(buzzer, 900, 100);
    delay(50);
}

void loop()
{
    ActionStation();
}

void ActionStation()
{
    //tone(buzzer, 750, 500);
    // Selects relevant station
    switch (station)
    {
    case 1:

        tone(buzzer, 2500, 250);
        delay(250);

        // Exercise
        Exercise(station1_duration);

        // Rest
        Rest(station1_rest);
        break;

    case 2:
        tone(buzzer, 2500, 250);
        delay(250);
        tone(buzzer, 2500, 250);
        delay(250);

        // Exercise
        Exercise(station2_duration);

        // Rest
        Rest(station2_rest);
        break;

    case 3:
        tone(buzzer, 2500, 250);
        delay(250);
        tone(buzzer, 2500, 250);
        delay(250);
        tone(buzzer, 2500, 250);
        delay(250);

        // Exercise
        Exercise(station3_duration);

        // Rest
        Rest(station3_rest);
        break;

    default:
        break;
    }
}

boolean handle_button()
{
    int button_pressed = !digitalRead(BUTTON_PIN); // pin low -> pressed
    return button_pressed;
}

void IncrementStation()
{
    tone(buzzer, 1500, 100);
    delay(50);
    tone(buzzer, 1600, 100);
    delay(50);
    tone(buzzer, 1700, 100);
    delay(50);
    tone(buzzer, 1800, 100);
    delay(50);
    tone(buzzer, 1900, 100);
    delay(50);
    tone(buzzer, 2000, 100);
    delay(50);
    tone(buzzer, 2100, 100);
    delay(50);
    tone(buzzer, 2200, 100);
    delay(50);

    // Increment station
    station++;

    // Check for out of bounds
    if (station > 3)
    {
        station = 1;
    }

    // Display
    Serial.println("********************************************************************");
    Serial.print("                           Station ");
    Serial.println(station);
    Serial.println("********************************************************************");

    // Reset timer
    timer = 0;

    // Start current station
    switch (station)
    {
    case 1:

        tone(buzzer, 2500, 250);
        delay(250);

        // Exercise
        Exercise(station1_duration);

        // Rest
        Rest(station1_rest);
        break;

    case 2:
        tone(buzzer, 2500, 250);
        delay(250);
        tone(buzzer, 2500, 250);
        delay(250);

        // Exercise
        Exercise(station2_duration);

        // Rest
        Rest(station2_rest);
        break;

    case 3:
        tone(buzzer, 2500, 250);
        delay(250);
        tone(buzzer, 2500, 250);
        delay(250);
        tone(buzzer, 2500, 250);
        delay(250);

        // Exercise
        Exercise(station3_duration);

        // Rest
        Rest(station3_rest);
        break;

    default:
        break;
    }
}

void IncrementTimer()
{
    delay(10);
    timer = timer + 0.01;
}

void LEDCOLOUR(int c)
{
    digitalWrite(latchPin, LOW);
    shiftOut(dataPin, clockPin, MSBFIRST, c);
    digitalWrite(latchPin, HIGH);
}

void Exercise(double duration)
{
    Serial.println("Start exercise");
    // Reset timer
    timer = 0;

    // 595
    LEDCOLOUR(19);

    while (timer < duration)
    {
        delay(100);
        timer = timer + 0.1;

        Serial.print("Exercise ");
        Serial.print(station);
        Serial.print(" - ");
        Serial.println(timer);

        // Button
        CheckButton();
    }
}

void Rest(double duration)
{

    //duration = duration / 2;          // TEMPORARY HACK!!!!!!!!!!!!!!!

    //tone(buzzer, 200, 500);

    Serial.println("Start rest");
    // Reset timer
    timer = 0;

    while (timer < duration)
    {

        // 595
        LEDCOLOUR(37);

        // Button
        CheckButton();

        // Timer
        delay(100);
        timer = timer + 0.1;

        Serial.print("Rest ");
        Serial.print(station);
        Serial.print(" - ");
        Serial.println(timer);

        // Button
        CheckButton();

        // 595 OFF
        LEDCOLOUR(0);

        // Timer
        delay(100);
        timer = timer + 0.1;

        Serial.print("Rest ");
        Serial.print(station);
        Serial.print(" - ");
        Serial.println(timer);

        // Button
        CheckButton();
    }
}

void CheckButton()
{

    // Button
    boolean button_pressed = handle_button();

    if (button_pressed)
    {
        tone(buzzer, 250, 150);
        delay(150);

        IncrementStation();
    }
}
