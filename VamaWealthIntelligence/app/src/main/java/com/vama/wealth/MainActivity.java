package com.vama.wealth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final int RED = Color.rgb(155, 45, 45);
    private LinearLayout body;
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences("vama_wealth", MODE_PRIVATE);
        showDashboard();
    }

    private TextView t(String s,int sp,boolean bold){
        TextView v=new TextView(this);
        v.setText(s); v.setTextSize(sp); v.setTextColor(GREEN);
        v.setPadding(20,14,20,14);
        if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return v;
    }

    private EditText input(String hint, boolean decimal){
        EditText e=new EditText(this);
        e.setHint(hint);
        e.setTextSize(17);
        e.setPadding(14,10,14,10);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | (decimal?InputType.TYPE_NUMBER_FLAG_DECIMAL:0) | InputType.TYPE_NUMBER_FLAG_SIGNED);
        return e;
    }

    private EditText textInput(String hint){
        EditText e=new EditText(this);
        e.setHint(hint); e.setTextSize(17); e.setPadding(14,10,14,10);
        return e;
    }

    private Button btn(String s){
        Button b=new Button(this);
        b.setText(s); b.setAllCaps(false); b.setTextSize(16);
        return b;
    }

    private void shell(String title){
        ScrollView sc=new ScrollView(this);
        body=new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(22,20,22,34);
        body.setBackgroundColor(BG);
        TextView brand=t("VAMA WEALTH INTELLIGENCE",20,true);
        brand.setTextColor(GOLD); body.addView(brand);
        body.addView(t(title,24,true));
        sc.addView(body); setContentView(sc);
    }

    private void back(){
        Button b=btn("← Dashboard");
        b.setOnClickListener(v->showDashboard());
        body.addView(b);
    }

    private View card(String a,String b){
        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL); c.setPadding(16,12,16,12);
        c.setBackgroundColor(Color.WHITE);
        c.addView(t(a,18,true)); c.addView(t(b,14,false));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,7,0,7); c.setLayoutParams(p); return c;
    }

    private void section(String title){
        body.addView(t(title,18,true));
    }

    private double val(EditText e) throws Exception {
        return Double.parseDouble(e.getText().toString().trim());
    }

    private String money(double x){
        return String.format(Locale.US,"₹%,.0f",x);
    }

    private void showDashboard(){
        shell("Decision Intelligence Dashboard");
        body.addView(t("Research • X-Ray • Simulate • Explain • Report • Monitor",14,false));
        body.addView(card("BUILD 2 — FUNCTIONAL LOCAL TOOLS","Live market/fund feeds are intentionally not fabricated. Calculators work with your inputs."));
        addModule("Clients","Add and track local client count/value.",()->clients());
        addModule("MF Research Lab","Analyze user-entered fund metrics.",()->researchLab());
        addModule("Compare Funds","Compare two funds using the same inputs.",()->compareFunds());
        addModule("Portfolio X-Ray","Measure concentration from portfolio weights.",()->portfolioXray());
        addModule("Digital Twin","Compare current vs target allocation scenarios.",()->digitalTwin());
        addModule("Stress Test","Apply equity/debt shock scenarios.",()->stressTest());
        addModule("Goals & SIP","SIP future-value illustration.",()->goalCalc());
        addModule("Vama AI / Evidence Mode","Build a traceable evidence record.",()->evidenceMode());
        addModule("What Changed?","Calculate before/after changes.",()->whatChanged());
        addModule("Reports","Create and share a local analysis summary.",()->reports());
        addModule("Smart URL / QR","Share a controlled URL now; QR backend remains pending.",()->smartUrl());
        addModule("Super Admin","Build, data-source and local-data controls.",()->superAdmin());
        TextView note=t("No live NAV, AMC holdings, market prices or recommendations are invented in this build.",12,false);
        note.setTextColor(RED); body.addView(note);
    }

    private void addModule(String name,String sub,final Runnable action){
        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL); c.setPadding(12,8,12,8); c.setBackgroundColor(Color.WHITE);
        TextView title=t(name,18,true); c.addView(title); c.addView(t(sub,13,false));
        c.setOnClickListener(v->action.run());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,6,0,6); c.setLayoutParams(p);
        body.addView(c);
    }

    private void clients(){
        shell("Clients"); back();
        final EditText name=textInput("Client name");
        final EditText value=input("Tracked portfolio value ₹",true);
        final TextView out=t("",16,true);
        Button add=btn("Add client locally");
        add.setOnClickListener(v->{
            try{
                String n=name.getText().toString().trim();
                double x=val(value);
                if(n.length()==0) throw new Exception();
                int count=prefs.getInt("client_count",0)+1;
                double total=Double.longBitsToDouble(prefs.getLong("client_total_bits",Double.doubleToLongBits(0)));
                total+=x;
                prefs.edit().putInt("client_count",count).putLong("client_total_bits",Double.doubleToLongBits(total)).apply();
                out.setText("Saved locally\nClients: "+count+"\nTracked value: "+money(total));
                name.setText(""); value.setText("");
            }catch(Exception e){out.setText("Enter a client name and portfolio value.");}
        });
        body.addView(name); body.addView(value); body.addView(add);
        int count=prefs.getInt("client_count",0);
        double total=Double.longBitsToDouble(prefs.getLong("client_total_bits",Double.doubleToLongBits(0)));
        out.setText("Clients: "+count+"\nTracked value: "+money(total));
        body.addView(out);
    }

    private void researchLab(){
        shell("MF Research Lab"); back();
        body.addView(t("User-entered metrics only — no fabricated live fund data.",13,false));
        EditText name=textInput("Fund name");
        EditText cagr=input("3-year CAGR %",true);
        EditText vol=input("Annualized volatility %",true);
        EditText dd=input("Maximum drawdown % (enter positive number)",true);
        EditText expense=input("Expense ratio %",true);
        TextView out=t("",15,false);
        Button analyze=btn("Analyze metrics");
        analyze.setOnClickListener(v->{
            try{
                double r=val(cagr), vv=val(vol), d=val(dd), ex=val(expense);
                double ratio=vv==0?0:r/vv;
                String n=name.getText().toString().trim(); if(n.length()==0)n="Selected fund";
                String riskBand=d<10?"Drawdown under 10%":(d<20?"Drawdown 10–20%":"Drawdown above 20%");
                out.setText(n+"\n3Y CAGR: "+r+"%\nVolatility: "+vv+"%\nMax drawdown: "+d+"%\nExpense: "+ex+"%\nReturn/volatility ratio: "+String.format(Locale.US,"%.2f",ratio)+"\n"+riskBand+"\n\nEvidence status: user-entered values; live source not connected.");
            }catch(Exception e){out.setText("Enter CAGR, volatility, drawdown and expense values.");}
        });
        body.addView(name);body.addView(cagr);body.addView(vol);body.addView(dd);body.addView(expense);body.addView(analyze);body.addView(out);
    }

    private void compareFunds(){
        shell("Compare Funds"); back();
        section("Fund A");
        EditText aName=textInput("Fund A name"); EditText aRet=input("A: 3Y CAGR %",true); EditText aVol=input("A: volatility %",true); EditText aExp=input("A: expense %",true);
        section("Fund B");
        EditText bName=textInput("Fund B name"); EditText bRet=input("B: 3Y CAGR %",true); EditText bVol=input("B: volatility %",true); EditText bExp=input("B: expense %",true);
        TextView out=t("",15,false); Button go=btn("Compare");
        go.setOnClickListener(v->{
            try{
                double ar=val(aRet),av=val(aVol),ae=val(aExp),br=val(bRet),bv=val(bVol),be=val(bExp);
                String an=aName.getText().toString().trim(); if(an.length()==0)an="Fund A";
                String bn=bName.getText().toString().trim(); if(bn.length()==0)bn="Fund B";
                out.setText(an+" — CAGR "+ar+"%, volatility "+av+"%, expense "+ae+"%, return/vol "+String.format(Locale.US,"%.2f",av==0?0:ar/av)+"\n\n"+
                        bn+" — CAGR "+br+"%, volatility "+bv+"%, expense "+be+"%, return/vol "+String.format(Locale.US,"%.2f",bv==0?0:br/bv)+"\n\n"+
                        "Differences (A − B): CAGR "+String.format(Locale.US,"%.2f",ar-br)+" pts, volatility "+String.format(Locale.US,"%.2f",av-bv)+" pts, expense "+String.format(Locale.US,"%.2f",ae-be)+" pts.\nNo winner is declared; this is an evidence comparison.");
            }catch(Exception e){out.setText("Enter all six numeric metrics.");}
        });
        body.addView(aName);body.addView(aRet);body.addView(aVol);body.addView(aExp);body.addView(bName);body.addView(bRet);body.addView(bVol);body.addView(bExp);body.addView(go);body.addView(out);
    }

    private void portfolioXray(){
        shell("Portfolio X-Ray"); back();
        body.addView(t("Enter portfolio allocation percentages for the three largest holdings.",14,false));
        EditText total=input("Portfolio value ₹",true);
        EditText w1=input("Largest holding weight %",true);
        EditText w2=input("Second holding weight %",true);
        EditText w3=input("Third holding weight %",true);
        TextView out=t("",16,false); Button go=btn("Run X-Ray");
        go.setOnClickListener(v->{
            try{
                double tv=val(total),a=val(w1),b=val(w2),c=val(w3),sum=a+b+c;
                if(a<0||b<0||c<0||sum>100) throw new Exception();
                double others=100-sum;
                double hhi=a*a+b*b+c*c+others*others;
                out.setText("Portfolio: "+money(tv)+"\nTop-3 concentration: "+String.format(Locale.US,"%.1f",sum)+"%\nOther holdings combined: "+String.format(Locale.US,"%.1f",others)+"%\nApprox. concentration index (HHI): "+String.format(Locale.US,"%.0f",hhi)+"\nTop holding value: "+money(tv*a/100.0)+"\n\nFor full overlap/sector/stock X-Ray, verified portfolio holdings data must be imported.");
            }catch(Exception e){out.setText("Enter valid weights totaling 100% or less.");}
        });
        body.addView(total);body.addView(w1);body.addView(w2);body.addView(w3);body.addView(go);body.addView(out);
    }

    private void digitalTwin(){
        shell("Digital Twin"); back();
        EditText value=input("Current portfolio ₹",true);
        EditText currentEq=input("Current equity allocation %",true);
        EditText targetEq=input("Target equity allocation %",true);
        EditText eqRet=input("Assumed annual equity return %",true);
        EditText debtRet=input("Assumed annual debt return %",true);
        EditText years=input("Years",false);
        TextView out=t("",16,false); Button go=btn("Simulate both allocations");
        go.setOnClickListener(v->{
            try{
                double pv=val(value),ce=val(currentEq)/100.0,te=val(targetEq)/100.0,er=val(eqRet)/100.0,dr=val(debtRet)/100.0,y=val(years);
                if(ce<0||ce>1||te<0||te>1||y<0)throw new Exception();
                double cr=ce*er+(1-ce)*dr, tr=te*er+(1-te)*dr;
                double cf=pv*Math.pow(1+cr,y), tf=pv*Math.pow(1+tr,y);
                out.setText("Current-allocation scenario: "+money(cf)+"\nTarget-allocation scenario: "+money(tf)+"\nScenario difference: "+money(tf-cf)+"\n\nAssumptions only; not a return guarantee.");
            }catch(Exception e){out.setText("Enter valid portfolio, allocations, assumptions and years.");}
        });
        body.addView(value);body.addView(currentEq);body.addView(targetEq);body.addView(eqRet);body.addView(debtRet);body.addView(years);body.addView(go);body.addView(out);
    }

    private void stressTest(){
        shell("Stress Test"); back();
        EditText value=input("Portfolio value ₹",true);
        EditText equity=input("Equity allocation %",true);
        EditText debt=input("Debt allocation %",true);
        EditText eqShock=input("Equity shock % (example -20)",true);
        EditText debtShock=input("Debt shock % (example -2)",true);
        TextView out=t("",17,true); Button go=btn("Run stress test");
        go.setOnClickListener(v->{
            try{
                double pv=val(value),e=val(equity),d=val(debt),es=val(eqShock),ds=val(debtShock);
                if(e<0||d<0||e+d>100)throw new Exception();
                double other=100-e-d;
                double impact=pv*((e/100.0)*(es/100.0)+(d/100.0)*(ds/100.0));
                double stressed=pv+impact;
                double pct=pv==0?0:(impact/pv*100.0);
                out.setText("Stressed value: "+money(stressed)+"\nImpact: "+money(impact)+" ("+String.format(Locale.US,"%.2f",pct)+"%)\nUnshocked/other allocation: "+String.format(Locale.US,"%.1f",other)+"%\n\nScenario only; no forecast or guarantee.");
            }catch(Exception e){out.setText("Enter valid allocations and shock percentages.");}
        });
        body.addView(value);body.addView(equity);body.addView(debt);body.addView(eqShock);body.addView(debtShock);body.addView(go);body.addView(out);
    }

    private void goalCalc(){
        shell("Goals & SIP"); back();
        body.addView(t("SIP Future Value — illustrative calculator",18,true));
        EditText sip=input("Monthly SIP ₹",true);
        EditText years=input("Years",false);
        EditText rate=input("Assumed annual return %",true);
        TextView out=t("",17,true); Button c=btn("Calculate illustration");
        c.setOnClickListener(v->{ try{
            double s=val(sip), r=val(rate)/1200.0; int n=(int)Math.round(val(years)*12);
            double fv=r==0?s*n:s*((Math.pow(1+r,n)-1)/r)*(1+r);
            out.setText("Illustrative value: "+money(fv)+"\nAssumption only; not a return guarantee.");
        }catch(Exception e){out.setText("Enter monthly SIP, years and assumed return.");}});
        body.addView(sip);body.addView(years);body.addView(rate);body.addView(c);body.addView(out);
    }

    private void evidenceMode(){
        shell("Vama AI / Evidence Mode"); back();
        body.addView(t("Build a traceable research record even before the live AI/data provider is connected.",14,false));
        EditText question=textInput("Research question / finding");
        EditText source=textInput("Source / document");
        EditText date=textInput("Source date");
        EditText calc=textInput("Calculation / method");
        EditText explanation=textInput("Explanation");
        TextView out=t("",15,false); Button go=btn("Create evidence record");
        go.setOnClickListener(v->{
            String q=question.getText().toString().trim();
            if(q.length()==0){out.setText("Enter a research question or finding.");return;}
            out.setText("QUESTION / FINDING\n"+q+"\n\nSOURCE\n"+source.getText()+"\nDATE\n"+date.getText()+"\nCALCULATION\n"+calc.getText()+"\nEXPLANATION\n"+explanation.getText()+"\n\nLive AI answer generation is not enabled until a verified provider is connected.");
        });
        body.addView(question);body.addView(source);body.addView(date);body.addView(calc);body.addView(explanation);body.addView(go);body.addView(out);
    }

    private void whatChanged(){
        shell("What Changed?"); back();
        EditText oldAlloc=input("Previous allocation %",true);
        EditText newAlloc=input("Current allocation %",true);
        EditText oldExpense=input("Previous expense %",true);
        EditText newExpense=input("Current expense %",true);
        TextView out=t("",16,false); Button go=btn("Calculate changes");
        go.setOnClickListener(v->{
            try{
                double oa=val(oldAlloc),na=val(newAlloc),oe=val(oldExpense),ne=val(newExpense);
                out.setText("Allocation change: "+String.format(Locale.US,"%+.2f",na-oa)+" percentage points\nExpense change: "+String.format(Locale.US,"%+.3f",ne-oe)+" percentage points\n\nProduction mode will add AMC holdings, manager, sector and benchmark changes from verified sources.");
            }catch(Exception e){out.setText("Enter previous and current allocation/expense values.");}
        });
        body.addView(oldAlloc);body.addView(newAlloc);body.addView(oldExpense);body.addView(newExpense);body.addView(go);body.addView(out);
    }

    private void reports(){
        shell("Reports"); back();
        EditText client=textInput("Client / portfolio name");
        EditText summary=textInput("Key analysis summary");
        EditText assumptions=textInput("Assumptions / methodology");
        TextView out=t("",15,false);
        Button gen=btn("Generate report summary");
        Button share=btn("Share report text");
        final String[] report={""};
        gen.setOnClickListener(v->{
            report[0]="VAMA WEALTH INTELLIGENCE\nAnalysis Report\n\nClient/Portfolio: "+client.getText()+"\n\nSummary: "+summary.getText()+"\n\nMethodology/Assumptions: "+assumptions.getText()+"\n\nDisclosure: Scenario and research support only; no guaranteed-return claim. Live source evidence must be attached where applicable.";
            out.setText(report[0]);
        });
        share.setOnClickListener(v->{
            if(report[0].length()==0){out.setText("Generate the report summary first.");return;}
            Intent i=new Intent(Intent.ACTION_SEND); i.setType("text/plain"); i.putExtra(Intent.EXTRA_TEXT,report[0]); startActivity(Intent.createChooser(i,"Share Vama report"));
        });
        body.addView(client);body.addView(summary);body.addView(assumptions);body.addView(gen);body.addView(share);body.addView(out);
    }

    private void smartUrl(){
        shell("Smart URL / QR"); back();
        EditText url=textInput("Paste report / website URL");
        TextView out=t("",15,false);
        Button share=btn("Share URL");
        share.setOnClickListener(v->{
            String u=url.getText().toString().trim();
            if(u.length()==0){out.setText("Enter a URL first.");return;}
            Intent i=new Intent(Intent.ACTION_SEND); i.setType("text/plain"); i.putExtra(Intent.EXTRA_TEXT,u); startActivity(Intent.createChooser(i,"Share Vama URL"));
        });
        body.addView(url);body.addView(share);
        body.addView(t("Programmable short-link rules and QR generation require the Vama backend; this build provides working Android sharing without pretending that backend is already live.",14,false));
        body.addView(out);
    }

    private void superAdmin(){
        shell("Super Admin"); back();
        int count=prefs.getInt("client_count",0);
        body.addView(card("App","Vama Wealth Intelligence Build 2 • Android 6/API 23+"));
        body.addView(card("Verified live data","Not connected — prevents fabricated NAV/market values."));
        body.addView(card("Local client records",String.valueOf(count)));
        Button reset=btn("Reset local client demo data");
        TextView out=t("",14,false);
        reset.setOnClickListener(v->{prefs.edit().clear().apply();out.setText("Local demo data reset.");});
        body.addView(reset);body.addView(out);
    }

    @Override public void onBackPressed(){ showDashboard(); }
}
