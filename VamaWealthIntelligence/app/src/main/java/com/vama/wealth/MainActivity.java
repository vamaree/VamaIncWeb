package com.vama.wealth;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int GREEN = Color.rgb(16, 59, 42);
    private static final int GOLD = Color.rgb(184, 145, 60);
    private static final int BG = Color.rgb(246, 247, 244);
    private LinearLayout body;

    private final String[] modules = {
        "Clients","MF Research Lab","Compare Funds","Portfolio X-Ray","Digital Twin",
        "Stress Test","Goals & SIP","Vama AI","What Changed?","Reports","Smart URL / QR","Super Admin"
    };

    @Override public void onCreate(Bundle b) { super.onCreate(b); showDashboard(); }

    private TextView t(String s,int sp,boolean bold){
        TextView v=new TextView(this); v.setText(s); v.setTextSize(sp); v.setTextColor(GREEN);
        v.setPadding(20,14,20,14); if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return v;
    }

    private Button btn(String s){
        Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(15);
        b.setOnClickListener(v->showModule(s)); return b;
    }

    private void shell(String title){
        ScrollView sc=new ScrollView(this); body=new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL); body.setPadding(22,22,22,32); body.setBackgroundColor(BG);
        TextView brand=t("VAMA WEALTH INTELLIGENCE",21,true); brand.setTextColor(GOLD); body.addView(brand);
        body.addView(t(title,24,true)); sc.addView(body); setContentView(sc);
    }

    private void showDashboard(){
        shell("Decision Intelligence Dashboard");
        body.addView(t("Research • X-Ray • Simulate • Explain • Report • Monitor",14,false));
        body.addView(card("Portfolio Intelligence","Concentration, overlap, risk and underlying exposure."));
        body.addView(card("Client Impact Radar","Connect fund and market changes to affected client portfolios."));
        body.addView(card("Evidence Mode","Source → Date → Calculation → Explanation → Original Data."));
        for(String m:modules) body.addView(btn(m));
        body.addView(t("V1 demonstration shell. Live investment data is not connected yet.",12,false));
    }

    private View card(String a,String b){
        LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(18,14,18,14);
        c.setBackgroundColor(Color.WHITE); c.addView(t(a,18,true)); c.addView(t(b,14,false));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); c.setLayoutParams(p); return c;
    }

    private void showModule(String m){
        shell(m); Button back=btn("← Dashboard"); back.setOnClickListener(v->showDashboard()); body.addView(back);
        if("Goals & SIP".equals(m)){ goalCalc(); return; }
        String d="Core V1 module ready for verified data/service integration.";
        if("Compare Funds".equals(m)) d="Compare up to 10 funds across returns, rolling returns, volatility, drawdown, cost, AUM, holdings and overlap.";
        else if("Portfolio X-Ray".equals(m)) d="Portfolio concentration, duplicate exposure, sectors, market cap, underlying holdings and risk flags.";
        else if("Digital Twin".equals(m)) d="Simulate replacement, rebalancing and allocation changes before changing the real portfolio.";
        else if("Stress Test".equals(m)) d="Test Nifty, midcap, banking, IT, rate, INR and custom shock scenarios.";
        else if("Vama AI".equals(m)) d="Evidence-backed research copilot. Production answers require verified sources and dated evidence.";
        else if("What Changed?".equals(m)) d="Track holdings, sector, manager, expense and category changes with client impact.";
        else if("Reports".equals(m)) d="Generate research, X-Ray, goals, comparison, change and stress reports with methodology and disclosures.";
        body.addView(t(d,16,false));
    }

    private void goalCalc(){
        body.addView(t("SIP Future Value — illustrative calculator",18,true));
        EditText sip=new EditText(this); sip.setHint("Monthly SIP ₹"); sip.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText years=new EditText(this); years.setHint("Years"); years.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText rate=new EditText(this); rate.setHint("Assumed annual return %"); rate.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextView out=t("",17,true); Button c=btn("Calculate illustration");
        c.setOnClickListener(v->{ try{
            double s=Double.parseDouble(sip.getText().toString()), r=Double.parseDouble(rate.getText().toString())/1200.0;
            int n=Integer.parseInt(years.getText().toString())*12;
            double fv=r==0?s*n:s*((Math.pow(1+r,n)-1)/r)*(1+r);
            out.setText(String.format(Locale.US,"Illustrative value: ₹%,.0f\nAssumption only; not a return guarantee.",fv));
        }catch(Exception e){out.setText("Enter all three values.");}});
        body.addView(sip); body.addView(years); body.addView(rate); body.addView(c); body.addView(out);
    }

    @Override public void onBackPressed(){ showDashboard(); }
}
