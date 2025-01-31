package com.shahzadafridi.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shahzadafridi.calendarview.Util.dp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarView : LinearLayout, CalenderViewInterface {

    // how many days to show, defaults to six weeks, 42 days
    private val DAYS_COUNT = 42

    //Date format                                      //Default
    private var dateFormat: String? = null;            private val DATE_FORMAT: String = "yyyy"

    //Background                                       //Default
    private var cv_bg: Int? = null;                    private var CALENDER_VIEW_BG: Int = R.drawable.rect_lr_wround_bg

    //Month                                            //Default
    private var month_font: Int? = null;               private var MONTH_FONT: Int = R.font.pfd_cond_regular
    private var month_txt_clr: Int? = null;            private var MONTH_TEXT_COLOR: Int = R.color.cblack
    private var month_txt_size: Int? = null;           private var MONTH_TEXT_SIZE: Int = 16
    private var month_selected_txt_clr: Int? = null;   private var MONTH_SELECTED_TEXT_COLOR: Int = R.color.cblack
    private var month_unselected_txt_clr: Int? = null; private var MONTH_UNSELECTED_TEXT_COLOR: Int = R.color.greyed_out
    private var month_bg: Int? = null;                 private var MONTH_BG: Int = R.color.cwhite

    //Week                                             //Default
    private var week_font: Int? = null;                private var WEEK_TEXT_COLOR: Int = R.color.cblack
    private var week_txt_clr: Int? = null;             private var WEEK_BG_COLOR: Int = R.color.cwhite
    private var week_bg_clr: Int? = null;              private var WEEK_FONT: Int = R.font.pfd_cond_regular
    private var week_txt_size: Int? = null;            private var WEEK_TEXT_SIZE: Int = 16

    //Day                                              //Default
    private var day_font: Int? = null;                 private var DAY_FONT: Int = R.font.pfd_cond_regular
    private var day_size: Int? = null;                 private var DAY_BG: Int = R.color.summer
    private var day_bg: Int? = null;                   private var DAY_SIZE: Int = 14
    private var day_txt_clr: Int? = null;              private var DAY_TEXT_COLOR: Int = R.color.cblack
    private var day_txt_size: Int? = null;             private var DAY_TEXT_SIZE: Int = 14
    private var day_selected_txt_clr: Int? = null;     private var DAY_SELECTED_TEXT_COLOR: Int = R.color.cwhite
    private var day_selected_bg: Int? = null;            private var DAY_SELECTED_BG: Int = R.color.cblack

    // internal components
    private var yearLayout: RelativeLayout? = null
    private var backIv: ImageView? = null
    private var weekLayout: LinearLayout? = null
    private var yearTv: TextView? = null
    private var calendarDayRv: RecyclerView? = null
    private var calendarMonthRv: RecyclerView? = null
    private var adapter: CalendarAdapter? = null
    private var adapterMonth: MonthAdapter? = null
    private var dayConfig: DayConfiguration = DayConfiguration()
    private var monthConfig: MonthConfiguration = MonthConfiguration()

    // current displayed month
    private val currentDate = Calendar.getInstance()

    //event handling
    private var eventHandler: CalenderViewInterface.EventHandler? = null

    // seasons' rainbow
    var rainbow = intArrayOf(
        R.color.summer,
        R.color.fall,
        R.color.winter,
        R.color.spring
    )

    //Months
    var monthSeason = intArrayOf(2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2)

    var monthNumber: Int = currentDate.get(Calendar.MONTH)

    var events: HashSet<Calendar>? = hashSetOf()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initControl(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initControl(context, attrs)
    }

    /**
     * Load control xml layout
     */
    private fun initControl(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.control_calendar, this)
        loadDateFormat(attrs)
        assignUiElements()
        buildCalendar()
    }

    private fun loadDateFormat(attrs: AttributeSet?) {
        val calenderViewAttr = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        try {
            //Date Formate
            dateFormat = calenderViewAttr.getString(R.styleable.CalendarView_dateFormat)
            //Background
            cv_bg = calenderViewAttr.getResourceId(R.styleable.CalendarView_cv_bg, CALENDER_VIEW_BG)
            /*
            //Month
            dateFormat = calenderViewAttr.getString(R.styleable.CalendarView_dateFormat)
            month_font = calenderViewAttr.getResourceId(R.styleable.CalendarView_month_font, MONTH_FONT)
            month_txt_clr = calenderViewAttr.getResourceId(R.styleable.CalendarView_month_txt_clr, MONTH_TEXT_COLOR)
            month_txt_size = calenderViewAttr.getResourceId(R.styleable.CalendarView_month_txt_size, MONTH_TEXT_SIZE)
            month_selected_txt_clr = calenderViewAttr.getResourceId(R.styleable.CalendarView_month_selected_txt_clr, MONTH_SELECTED_TEXT_COLOR)
            month_unselected_txt_clr = calenderViewAttr.getResourceId(R.styleable.CalendarView_month_unselect_txt_clr, MONTH_UNSELECTED_TEXT_COLOR)
            month_bg = calenderViewAttr.getResourceId(R.styleable.CalendarView_month_bg, MONTH_BG)
            //Week
            week_font = calenderViewAttr.getResourceId(R.styleable.CalendarView_week_font, WEEK_FONT)
            week_txt_clr = calenderViewAttr.getResourceId(R.styleable.CalendarView_week_txt_clr, WEEK_TEXT_COLOR)
            week_bg_clr = calenderViewAttr.getResourceId(R.styleable.CalendarView_week_bg_clr, WEEK_BG_COLOR)
            week_txt_size = calenderViewAttr.getResourceId(R.styleable.CalendarView_week_txt_size, WEEK_TEXT_SIZE)
            //Day
            day_font = calenderViewAttr.getResourceId(R.styleable.CalendarView_day_font, DAY_FONT)
            day_size = calenderViewAttr.getResourceId(R.styleable.CalendarView_day_size, DAY_SIZE)
            day_txt_clr = calenderViewAttr.getResourceId(R.styleable.CalendarView_day_txt_clr, DAY_TEXT_COLOR)
            day_txt_size = calenderViewAttr.getResourceId(R.styleable.CalendarView_day_text_size, DAY_TEXT_SIZE)
            day_bg = calenderViewAttr.getResourceId(R.styleable.CalendarView_day_bg, DAY_BG)
            day_select_bg = calenderViewAttr.getResourceId(R.styleable.CalendarView_day_select_bg, DAY_SELECTED_BG)
            day_selected_txt_clr = calenderViewAttr.getResourceId(R.styleable.CalendarView_day_select_txt_clr, DAY_SELECTED_TEXT_COLOR)

            */

        } finally {
            calenderViewAttr.recycle()
        }
    }

    private fun assignUiElements() {
        // layout is inflated, assign local variables to components
        yearLayout = findViewById(R.id.calendar_year_rl)
        backIv = findViewById(R.id.calendar_back)
        weekLayout = findViewById(R.id.calendar_week_ll)
        yearTv = findViewById(R.id.calendar_year_tv)
        calendarDayRv = findViewById(R.id.calendar_day_rv)
        calendarDayRv!!.setHasFixedSize(true)
        calendarDayRv!!.layoutManager = GridLayoutManager(context, 7)
        calendarMonthRv = findViewById(R.id.calendar_month_rv)
        calendarMonthRv!!.setHasFixedSize(true)
        calendarMonthRv!!.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
    }

    override fun onMonthClick(view: View?, month: String, position: Int) {
        monthNumber = position
        currentDate.set(Calendar.MONTH, monthNumber)
        buildCalendar()
        eventHandler?.onMonthClick(view,month,position)
    }

    /**
     * Display dates correctly in grid
     */
    override fun buildCalendar(): CalendarView {
        val cells = ArrayList<Calendar>()
        val calendar = currentDate.clone() as Calendar

        // determine the cell for current month's beginning
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar[Calendar.DAY_OF_MONTH] = 1
        val monthBeginningCell = calendar[Calendar.DAY_OF_WEEK] - 1

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        // fill cells
        while (cells.size < DAYS_COUNT) {
            cells.add(Calendar.getInstance().apply {
                this.time = calendar.time
            })
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // update grid
        adapter = CalendarAdapter(context, cells, events, eventHandler, dayConfig,monthNumber)
        calendarDayRv!!.adapter = adapter

        //update months
        adapterMonth = MonthAdapter(context,this,monthConfig,Util.months,monthNumber)
        calendarMonthRv!!.adapter = adapterMonth

        val sdfYear = if (dateFormat != null) SimpleDateFormat(dateFormat) else SimpleDateFormat(DATE_FORMAT)
        yearTv!!.text = sdfYear.format(currentDate.time)

        // set header color according to current season
        // val month = currentDate[Calendar.MONTH]
        // val season = monthSeason[month]
        // val color = rainbow[season]
        // header!!.setBackgroundColor(ContextCompat.getColor(context, color))

        return this
    }

    /**
     * Assign event handler to be passed needed events
     */
    fun setEventHandler(eventHandler: CalenderViewInterface.EventHandler?) {
        this.eventHandler = eventHandler
        adapter!!.setEventHandler(eventHandler!!)
    }

    /**
     * Interface Methods
     */

    override fun builder(): CalendarView {
        return this
    }

    override fun withBackButton(isShow: Boolean, background: Int?): CalendarView {
        background?.let {
            backIv!!.setImageResource(it)
        }
        if (isShow){
            backIv!!.visibility = View.VISIBLE
        }else{
            backIv!!.visibility = View.INVISIBLE
        }
        backIv!!.setOnClickListener { view ->
            eventHandler?.onBackClick(view)
        }
        return this
    }

    override fun withEvents(events: HashSet<Calendar>?,eventDotColor: Int?): CalendarView {
        this.events = events
        dayConfig.eventDotColor = eventDotColor
        return this
    }

    override fun withYearPanel(
        dateFormat: String?,
        textColor: Int?,
        textSize: Int?,
        font: Int?
    ): CalendarView {
        this.dateFormat = dateFormat
        textSize?.let {
            yearTv!!.textSize = it.toFloat()
        }
        textColor?.let { clr ->
            yearTv!!.setTextColor(ContextCompat.getColor(context, clr))
        }
        font?.let {
            yearTv!!.typeface = ResourcesCompat.getFont(context, it)
        }
        return this
    }

    override fun withYearPanleMargin(
        top: Int,
        bottom: Int,
        left: Int,
        right: Int
    ): CalendarView {
        setMargin(yearLayout!!, left, right, top, bottom)
        return this
    }

    override fun withMonthPanel(
        font: Int?,
        textSize: Int?,
        selectedTextColor: Int?,
        unSelectedTextColor: Int?,
        background: Int?,
        months: ArrayList<String>?
    ): CalendarView {
        if (months != null) {
            Util.months = months
        }
        month_font = font
        month_txt_size = textSize
        month_selected_txt_clr = selectedTextColor
        month_unselected_txt_clr = unSelectedTextColor
        month_bg = background
        monthConfig.mBg = month_bg
        monthConfig.mSelectedClr = month_selected_txt_clr
        monthConfig.mUnSelectedClr = month_unselected_txt_clr
        monthConfig.mFont = month_font
        monthConfig.mTxtSize = month_txt_size
        month_font?.let {
            yearTv!!.typeface = ResourcesCompat.getFont(context, it)
        }
        month_bg?.let {
            calendarMonthRv!!.background = ContextCompat.getDrawable(context, it)
        }
        return this
    }

    override fun withMonthPanleMargin(
        top: Int,
        bottom: Int,
        left: Int,
        right: Int
    ): CalendarView {
        setMargin(calendarMonthRv!!, left, right, top, bottom)
        return this
    }

    override fun withWeekPanel(font: Int?, textColor: Int?, textSize: Int?, background: Int?,weekDays: ArrayList<String>?): CalendarView {
        if (weekDays != null) {
            Util.weekDays = weekDays
        }
        week_font = font
        week_txt_clr = textColor
        week_txt_size = textSize
        week_bg_clr = background

        var i = 0

        weekLayout!!.children.iterator().forEach {
            week_font?.let { font ->
                (it as TextView).typeface = ResourcesCompat.getFont(context, font)
            }
            week_txt_clr?.let { clr ->
                (it as TextView).setTextColor(ContextCompat.getColor(context, clr))
            }
            week_txt_size?.let { size ->
                (it as TextView).setTextSize(size.toFloat())
            }
            (it as TextView).text = Util.weekDays[i]
            i++
        }

        week_bg_clr?.let {
            weekLayout!!.setBackgroundColor(ContextCompat.getColor(context, it))
        }
        return this
    }

    override fun withWeekPanelMargin(
        top: Int,
        bottom: Int,
        left: Int,
        right: Int
    ): CalendarView {
        setMargin(weekLayout!!, left, right, top, bottom)
        return this
    }


    override fun withDayPanel(
        font: Int?,
        textColor: Int?,
        textSize: Int?,
        selectedTextColor: Int?,
        selectedBackground: Int?,
        background: Int?
    ): CalendarView {
        day_font = font
        day_txt_clr = textColor
        day_txt_size = textSize
        day_selected_txt_clr = selectedTextColor
        day_selected_bg = selectedBackground
        day_bg = background
        dayConfig.dayTxtClr = day_txt_clr
        dayConfig.dayBg = day_bg
        dayConfig.daySelectedClr = day_selected_txt_clr
        dayConfig.daySelectedBg = day_selected_bg
        dayConfig.dayFont = day_font
        dayConfig.dayTxtSize = day_txt_size
        return this
    }

    override fun withDayPanelMargin(
        top: Int,
        bottom: Int,
        left: Int,
        right: Int
    ): CalendarView {
        setMargin(calendarDayRv!!, left, right, top, bottom)
        return this
    }

    override fun withCalenderViewBg(background: Int?): CalendarView {
        cv_bg = background
        this.background = ContextCompat.getDrawable(context, cv_bg!!)
        return this
    }

    //Set Margin to view.
    fun setMargin(view: View, left: Int, right: Int, top: Int, bottom: Int) {
        val params = view.getLayoutParams() as LinearLayout.LayoutParams
        params.setMargins(left.dp, top.dp, right.dp, bottom.dp)
        view.setLayoutParams(params)
    }

}
