 package com.example.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Quiz.db";
    private static final int DATABASE_VERSION = 1;

    private static QuizDbHelper instance;

    private SQLiteDatabase db;

    private QuizDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                QuizContract.CategoriesTable.TABLE_NAME + "( " +
                QuizContract.CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.CategoriesTable.COLUMN_NAME + " TEXT " +
                ")";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizContract.QuestionsTable.TABLE_NAME + " (" +
                QuizContract.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_ANSWER_NO + " INTEGER, " +
                QuizContract.QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuizContract.QuestionsTable.COLUMN_CATEGORY_ID + ") REFERENCES " +
                QuizContract.CategoriesTable.TABLE_NAME + "(" + QuizContract.CategoriesTable._ID + ")" + "ON DELETE CASCADE" +
                ")";

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillCategoriesTable();
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable() {
        Category c1 = new Category("Programming");
        addCategory(c1);
        Category c2 = new Category("Math");
        addCategory(c2);
        Category c3 = new Category("Spanish");
        addCategory(c3);
    }

    private void addCategory(Category category) {
        ContentValues cv = new ContentValues();
        cv.put(QuizContract.CategoriesTable.COLUMN_NAME, category.getName());
        db.insert(QuizContract.CategoriesTable.TABLE_NAME, null, cv);
    }

    private void fillQuestionsTable() {
        Question q1 = new Question("Which of these data structures requires O(1) time complexity to remove an element from its end?", "Linked List", "Tree", "Dequeue", 3, Question.DIFFICULTY_EASY, Category.PROGRAMMING);
        addQuestion(q1);
        Question q2 = new Question("An nxn matrix is said to be non-invertible if its determinant is equal to: ", "-1", "1", "0", 3, Question.DIFFICULTY_EASY, Category.MATH);
        addQuestion(q2);
        Question q3 = new Question("Three different vectors in R^3 forms a basis for R^3 if and only if: ", "They are zero vectors", "They are linearly independent", "They are linearly dependent", 2, Question.DIFFICULTY_EASY, Category.MATH);
        addQuestion(q3);
        Question q4 = new Question("What will the print(9//2) operation print out in python", "4.5", "5", "4", 3, Question.DIFFICULTY_HARD, Category.PROGRAMMING);
        addQuestion(q4);
        Question q5 = new Question("¿Cómo se dice \"pencil\" en español?", "lápiz", "libro", "playa", 1, Question.DIFFICULTY_EASY, Category.SPANISH);
        addQuestion(q5);
        Question q6 = new Question("Which of this operators has the left to right associativity?", "Unary", "Logical Not", "Parentheses", 3, Question.DIFFICULTY_HARD, Category.PROGRAMMING);
        addQuestion(q6);
        Question q7 = new Question("How many words can be built using all the letters once in \"GREECE\"?", "120", "720", "24", 1, Question.DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q7);
        Question q8 = new Question("Which of these languages is not used in Android development?", "Java", "Kotlin", "HTML", 3, Question.DIFFICULTY_EASY, Category.PROGRAMMING);
        addQuestion(q8);
    }

    private void addQuestion(Question q) {
        ContentValues cv = new ContentValues();
        cv.put(QuizContract.QuestionsTable.COLUMN_QUESTION, q.getQuestion());
        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION1, q.getOption1());
        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION2, q.getOption2());
        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION3, q.getOption3());
        cv.put(QuizContract.QuestionsTable.COLUMN_ANSWER_NO, q.getAnswerNo());
        cv.put(QuizContract.QuestionsTable.COLUMN_DIFFICULTY, q.getDifficulty());
        cv.put(QuizContract.QuestionsTable.COLUMN_CATEGORY_ID, q.getCategoryID());

        db.insert(QuizContract.QuestionsTable.TABLE_NAME, null, cv);
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuizContract.CategoriesTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(QuizContract.CategoriesTable._ID)));
                category.setName(c.getString(c.getColumnIndex(QuizContract.CategoriesTable.COLUMN_NAME)));
                categoryList.add(category);
            } while(c.moveToNext());
        }

        c.close();
        return categoryList;
    }

    public ArrayList<Question> getAllQuestions() {

        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery( "SELECT * FROM " + QuizContract.QuestionsTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNo(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NO)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }

    public ArrayList<Question> getQuestions(int categoryID, String difficulty) {

        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = QuizContract.QuestionsTable.COLUMN_CATEGORY_ID + " = ? " +
                " AND " + QuizContract.QuestionsTable.COLUMN_DIFFICULTY + " = ? ";
        String[] selectionArgs = new String[]{
                String.valueOf(categoryID), difficulty
        };

        Cursor c = db.query(
                QuizContract.QuestionsTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNo(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NO)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }
}





















