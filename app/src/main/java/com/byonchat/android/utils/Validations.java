package com.byonchat.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Window;

import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Rooms;
import com.byonchat.android.provider.Skin;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Validations {
    private static Validations instance = new Validations();
    static Context context;

    public Validations getInstance(Context ctx) {
        context = ctx;
        return instance;
    }

    public void changeProtectLogin(String usernameRoom, String proKey) {
        BotListDB botListDB = BotListDB.getInstance(context);
        Cursor cur = botListDB.getSingleRoom(usernameRoom);
        if (cur.getCount() > 0) {
            String nUsername = cur.getString(cur.getColumnIndex(BotListDB.ROOM_USERNAME));
            String nRealname = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
            String nCOntent = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
            String nColor = cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR));
            String nBackdrop = cur.getString(cur.getColumnIndex(BotListDB.ROOM_BACKDROP));
            String nLastupdate = cur.getString(cur.getColumnIndex(BotListDB.ROOM_LASTUPDATE));
            String nIcon = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));
            String firstT = cur.getString(cur.getColumnIndex(BotListDB.ROOM_FIRST_TAB));
            String colorT = cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR_TEXT));

            botListDB.deleteRoomsbyTAB(usernameRoom);
            JSONObject obj = new JSONObject();
            try {
                String a = new JSONObject(nColor).getString("a");
                String b = new JSONObject(nColor).getString("b");
                String c = new JSONObject(nColor).getString("c");
                String d = new JSONObject(nColor).getString("d");
                String e = new JSONObject(nColor).getString("e");


                obj.put("a", a);
                obj.put("b", b);
                obj.put("c", c);
                obj.put("d", d);
                obj.put("e", e);
                obj.put("p", proKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Rooms rooms = new Rooms(nUsername, nRealname, nCOntent, obj.toString(), nBackdrop, nLastupdate, nIcon, firstT, colorT);
            botListDB.insertRooms(rooms);
        }
    }

    public int getValidationLogin(int menit) {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);
        int hour_sys = Integer.parseInt(s[1].split(":")[0]);
        int min_sys = Integer.parseInt(s[1].split(":")[1]);

        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(1);
        if (cursor.getCount() > 0) {
            String time_strDB = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            String[] sDB = time_strDB.split(" ");
            int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
            int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
            int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);
            int hour_sysDB = Integer.parseInt(sDB[1].split(":")[0]);
            int min_sysDB = Integer.parseInt(sDB[1].split(":")[1]);

            if (year_sysDB == year_sys) {
                if (month_sysDB == month_sys) {
                    if (day_sysDB == day_sys) {
                        if (hour_sysDB == hour_sys) {
                            if ((min_sys - min_sysDB) > menit) {
                                error = 1;
                            } else {
                                error = 0;
                            }
                        } else {
                            error = 1;
                        }
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }

        } else {
            Interval interval = new Interval();
            interval.setId(1);
            interval.setTime(time_str);
            db.createContact(interval);
            error = 1;
        }

        db.close();

        if (error == 1) {
            setTime();
        }

        return error;
    }

    public int getValidationLoginById(int id) {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);
        int hour_sys = Integer.parseInt(s[1].split(":")[0]);
        int min_sys = Integer.parseInt(s[1].split(":")[1]);

        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(id);
        if (cursor.getCount() > 0) {
            String time_strDB = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            String[] sDB = time_strDB.split(" ");
            int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
            int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
            int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);
            int hour_sysDB = Integer.parseInt(sDB[1].split(":")[0]);
            int min_sysDB = Integer.parseInt(sDB[1].split(":")[1]);

            if (year_sysDB == year_sys) {
                if (month_sysDB == month_sys) {
                    if (day_sysDB == day_sys) {
                        if (hour_sysDB == hour_sys) {
                            if ((min_sys - min_sysDB) > 5) {
                                error = 1;
                            } else {
                                error = 0;
                            }
                        } else {
                            error = 1;
                        }
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }

        } else {
            Interval interval = new Interval();
            interval.setId(id);
            interval.setTime(time_str);
            db.createContact(interval);
            error = 1;
        }

        Log.w("salahA12", "sat==>" + error);
        db.close();

        return error;
    }

    public void setTimebyId(int id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(id);
        if (cursor.getCount() > 0) {
            db.deleteContact(id);
        }
        Interval interval = new Interval();
        interval.setId(id);
        interval.setTime(time_str);
        db.createContact(interval);
        db.close();
    }

    public void setTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(1);
        if (cursor.getCount() > 0) {
            db.deleteContact(1);
        }
        Interval interval = new Interval();
        interval.setId(1);
        interval.setTime(time_str);
        db.createContact(interval);
        db.close();
    }

    public BitmapDrawable header(Window window) {
        BitmapDrawable back_draw = null;
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if (cursorSelect.getCount() > 0) {
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c = db.getCountSkin();
            if (c.getCount() > 0) {
                skins = db.retriveSkinDetails(skin);
                Bitmap back_bitmap = FilteringImage.headerColor(window, context, Color.parseColor(skins.getColor()));
                back_draw = new BitmapDrawable(context.getResources(), back_bitmap);
            }
            c.close();
        }
        cursorSelect.close();
        db.close();
        return back_draw;

    }

    public BitmapDrawable headerCostume(Window window, String color) {
        BitmapDrawable back_draw = null;
        Bitmap back_bitmap = FilteringImage.headerColor(window, context, Color.parseColor(color));
        back_draw = new BitmapDrawable(context.getResources(), back_bitmap);
        return back_draw;

    }

    public BitmapDrawable logoCustome() {
        BitmapDrawable back_draw = null;
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if (cursorSelect.getCount() > 0) {
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c = db.getCountSkin();
            if (c.getCount() > 0) {
                skins = db.retriveSkinDetails(skin);
                Bitmap logo = skins.getLogo_header();
                back_draw = new BitmapDrawable(context.getResources(), logo);
            }
            c.close();
        }
        cursorSelect.close();
        db.close();
        return back_draw;
    }

    public String colorTheme(boolean bc) {
        String color = "#006b9c";
        if (bc == false) {
            IntervalDB db = new IntervalDB(context);
            db.open();
            Cursor cursorSelect = db.getSingleContact(4);
            if (cursorSelect.getCount() > 0) {
                String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
                Skin skins = null;
                Cursor c = db.getCountSkin();
                if (c.getCount() > 0) {
                    skins = db.retriveSkinDetails(skin);
                    color = skins.getColor();
                }
                c.close();
            }
            cursorSelect.close();
            db.close();
        }
        return color;
    }

    public String getTitle() {
        String title = "";
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if (cursorSelect.getCount() > 0) {
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c = db.getCountSkin();
            if (c.getCount() > 0) {
                skins = db.retriveSkinDetails(skin);
                title = skins.getTitle();
            }
            c.close();
        }
        cursorSelect.close();
        db.close();
        return title;

    }

    public boolean showSplash() {
        boolean show = false;
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(5);
        if (cursorSelect.getCount() > 0) {
            String time_strDB = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            if (time_strDB.equalsIgnoreCase("show")) {
                show = true;
                db.updateContact(5, "hide");
            }
        } else {
            show = true;
            Interval interval = new Interval();
            interval.setId(5);
            interval.setTime("hide");
            db.createContact(interval);
        }
        cursorSelect.close();
        db.close();
        return show;
    }

    public void setShowSplash() {
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(5);
        if (cursorSelect.getCount() > 0) {
            db.updateContact(5, "show");
        } else {
            Interval interval = new Interval();
            interval.setId(5);
            interval.setTime("show");
            db.createContact(interval);
        }
        cursorSelect.close();
        db.close();
    }

    public String IntervalResult(long id) {
        String result = null;
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(id);
        if (cursorSelect.getCount() > 0) {
            result = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
        }
        cursorSelect.close();
        db.close();

        return result;
    }

    public int getValidationRequestTheme() {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);
        int hour_sys = Integer.parseInt(s[1].split(":")[0]);
        int min_sys = Integer.parseInt(s[1].split(":")[1]);

        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(7);
        if (cursor.getCount() > 0) {
            String time_strDB = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            String[] sDB = time_strDB.split(" ");
            int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
            int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
            int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);
            int hour_sysDB = Integer.parseInt(sDB[1].split(":")[0]);
            int min_sysDB = Integer.parseInt(sDB[1].split(":")[1]);

            if (year_sysDB == year_sys) {
                if (month_sysDB == month_sys) {
                    if (day_sysDB == day_sys) {
                        error = 0;
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }

        } else {
            error = 1;
        }

        db.close();

        if (error == 1) {
            setTimeTheme();
        }

        return error;
    }

    public void setTimeTheme() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(7);
        if (cursor.getCount() > 0) {
            db.deleteContact(7);
        }
        Interval interval = new Interval();
        interval.setId(7);
        interval.setTime(time_str);
        db.createContact(interval);
        db.close();
    }

    public void setShow(long id) {
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(id);
        if (cursorSelect.getCount() > 0) {
            db.updateContact(id, "show");
        } else {
            Interval interval = new Interval();
            interval.setId(id);
            interval.setTime("show");
            db.createContact(interval);
        }
        cursorSelect.close();
        db.close();
    }

    public void setNotShow(long id) {
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(id);
        if (cursorSelect.getCount() > 0) {
            db.deleteContact(id);
        }
        cursorSelect.close();
        db.close();
    }

    public boolean getShow(long id) {
        boolean show = false;
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(id);
        if (cursorSelect.getCount() > 0) {
            show = true;
        }
        cursorSelect.close();
        db.close();

        return show;
    }


    public boolean getPlayStreaming(String Name) {
        boolean play = false;
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(9);
        if (cursorSelect.getCount() > 0) {
            if (cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME)).equalsIgnoreCase(Name)) {
                play = true;
            } else {
                play = false;
                db.updateContact(9, Name);
            }
        } else {
            Interval interval = new Interval();
            interval.setId(9);
            interval.setTime(Name);
            db.createContact(interval);
            play = true;
        }
        cursorSelect.close();
        db.close();

        return play;
    }

    public int getValidationById(int id, int menit) {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);
        int hour_sys = Integer.parseInt(s[1].split(":")[0]);
        int min_sys = Integer.parseInt(s[1].split(":")[1]);

        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(id);
        if (cursor.getCount() > 0) {
            String time_strDB = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            String[] sDB = time_strDB.split(" ");
            int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
            int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
            int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);
            int hour_sysDB = Integer.parseInt(sDB[1].split(":")[0]);
            int min_sysDB = Integer.parseInt(sDB[1].split(":")[1]);

            if (year_sysDB == year_sys) {
                if (month_sysDB == month_sys) {
                    if (day_sysDB == day_sys) {
                        if (hour_sysDB == hour_sys) {
                            if ((min_sys - min_sysDB) > menit) {
                                error = 1;
                            } else {
                                error = 0;
                            }
                        } else {
                            error = 1;
                        }
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }

        } else {
            Interval interval = new Interval();
            interval.setId(id);
            interval.setTime(time_str);
            db.createContact(interval);
            error = 1;
        }

        db.close();

        if (error == 1) {
            setTimeById(id);
        }

        return error;
    }

    public void setTimeById(int id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(id);
        if (cursor.getCount() > 0) {
            db.deleteContact(id);
        }
        Interval interval = new Interval();
        interval.setId(id);
        interval.setTime(time_str);
        db.createContact(interval);
        db.close();
    }

    public boolean cekRoom(String destination) {
        boolean oke = false;
        String regex = "[0-9]+";
        if (!destination.matches(regex)) {
            oke = true;
        }
        return oke;
    }

    public boolean setContentValidation(int id, String content) {
        boolean show = false;
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(id);
        if (cursorSelect.getCount() > 0) {
            db.updateContact(id, content);
        } else {
            show = true;
            Interval interval = new Interval();
            interval.setId(id);
            interval.setTime(content);
            db.createContact(interval);
        }
        cursorSelect.close();
        db.close();
        return show;
    }

    public String getContentValidation(int id) {
        String result = "";
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursorSelect = db.getSingleContact(id);
        if (cursorSelect.getCount() > 0) {
            result = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
        }
        cursorSelect.close();
        db.close();
        return result;
    }

    public int getShowOneDay(String times) {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);

        String time_strDB = times;
        String[] sDB = time_strDB.split(" ");
        int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
        int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
        int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);

        if (year_sysDB == year_sys) {
            if (month_sysDB == month_sys) {
                if (day_sysDB == day_sys) {
                    error = 0;
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }
        } else {
            error = 1;
        }

        return error;
    }

    public int getShowMinute(String times, int menit) {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);
        int hour_sys = Integer.parseInt(s[1].split(":")[0]);
        int min_sys = Integer.parseInt(s[1].split(":")[1]);

        String time_strDB = times;
        String[] sDB = time_strDB.split(" ");
        int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
        int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
        int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);
        int hour_sysDB = Integer.parseInt(sDB[1].split(":")[0]);
        int min_sysDB = Integer.parseInt(sDB[1].split(":")[1]);

        if (year_sysDB == year_sys) {
            if (month_sysDB == month_sys) {
                if (day_sysDB == day_sys) {
                    if (hour_sysDB == hour_sys) {
                        if ((min_sys - min_sysDB) > menit) {
                            error = 1;
                        } else {
                            error = 0;
                        }
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }
        } else {
            error = 1;
        }

        return error;
    }


    public int getValidationPerHari(String id) {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");
        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);

        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(Long.parseLong(id));
        if (cursor.getCount() > 0) {
            String time_strDB = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            String[] sDB = time_strDB.split(" ");
            int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
            int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
            int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);

            if (year_sysDB == year_sys) {
                if (month_sysDB == month_sys) {
                    if (day_sys == day_sysDB) {
                        error = 0;
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }

        } else {
            Interval interval = new Interval();
            interval.setId(Long.parseLong(id));
            interval.setTime(time_str);
            db.createContact(interval);
            error = 1;
        }

        db.close();

        if (error == 1) {
            setTimePerHari(id);
        }

        return error;
    }

    public void setTimePerHari(String id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        IntervalDB db = new IntervalDB(context);
        db.open();
        Cursor cursor = db.getSingleContact(Long.parseLong(id));
        if (cursor.getCount() > 0) {
            db.deleteContact(Long.parseLong(id));
        }
        Interval interval = new Interval();
        interval.setId(Long.parseLong(id));
        interval.setTime(time_str);
        db.createContact(interval);
        db.close();
    }

    public String numberToCurency(String nilai) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator('.');
        formatRp.setGroupingSeparator(',');

        double dump = 0;
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        try {
            dump = Double.parseDouble(nilai);
        } catch (Exception e) {
            dump = 0;
        }

        return kursIndonesia.format(dump);
    }

    public String numberToCurencyBack(String nilai) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        double dump = 0;
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        try {
            dump = Double.parseDouble(nilai);
        } catch (Exception e) {
            dump = 0;
        }

        return kursIndonesia.format(dump).split(",")[0];
    }


    public String numberToCurencyUSD(String nilai) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator('.');
        formatRp.setGroupingSeparator(',');

        double dump = 0;
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        try {
            dump = Double.parseDouble(nilai);
        } catch (Exception e) {
            dump = 0;
        }

        return kursIndonesia.format(dump).split(",")[0];
    }

    public String getSignatureProfilePicture(String jaberId, MessengerDatabaseHelper messengerDatabaseHelper) {

        Contact contact = messengerDatabaseHelper.getContact(jaberId);
        String signature = "";
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);
        if (contact == null) {
            return "11";
        } else {
            String date = contact.getFacebookid() != null ? contact.getFacebookid() : "";
            if (date.length() > 0) {
                long d = Long.parseLong(date);
                String datetime = dateFormat.format(d);
                String[] sDB = datetime.split(" ");
                int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
                int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
                int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);
                if (year_sysDB == year_sys) {
                    if (month_sysDB == month_sys) {
                        if (day_sysDB == day_sys) {
                            error = 0;
                        } else {
                            error = 1;
                        }
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }
        }


        if (error == 1) {
            Date convertedDate = new Date();
            long milliseconds = convertedDate.getTime();
            contact.setFacebookid(String.valueOf(milliseconds));
            messengerDatabaseHelper.updateData(contact);
        }

        signature = contact.getFacebookid();

        return signature;
    }


}
