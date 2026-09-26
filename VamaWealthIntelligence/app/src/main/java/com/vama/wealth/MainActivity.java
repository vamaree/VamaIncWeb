package com.vama.wealth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;
import java.security.MessageDigest;

public class MainActivity extends Activity {
    private static final int GREEN = Color.rgb(16,59,42);
    private static final int GOLD = Color.rgb(184,145,60);
    private static final int BG = Color.rgb(246,247,244);
    private static final int RED = Color.rgb(155,45,45);
    private LinearLayout body;
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        prefs=getSharedPreferences("vama_wealth",MODE_PRIVATE);
        showDashboard();
    }

    private TextView t(String s,int sp,boolean bold){
        TextView v=new TextView(this); v.setText(s); v.setTextSize(sp); v.setTextColor(GREEN);
        v.setPadding(18,12,18,12); if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return v;
    }

    private EditText num(String hint){
        EditText e=new EditText(this); e.setHint(hint); e.setTextSize(17); e.setPadding(12,8,12,8);
        e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        return e;
    }

    private EditText txt(String hint){
        EditText e=new EditText(this); e.setHint(hint); e.setTextSize(17); e.setPadding(12,8,12,8); return e;
    }

    private Button btn(String s){
        Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(16); return b;
    }

    private void shell(String title){
        ScrollView sc=new ScrollView(this);
        body=new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(20,18,20,32); body.setBackgroundColor(BG);
        TextView brand=t("VAMA WEALTH INTELLIGENCE",20,true); brand.setTextColor(GOLD); body.addView(brand);
        body.addView(t(title,24,true)); sc.addView(body); setContentView(sc);
    }

    private void back(){
        Button b=btn("← Dashboard"); b.setOnClickListener(v->showDashboard()); body.addView(b);
    }

    private View card(String a,String b){
        LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(14,10,14,10); c.setBackgroundColor(Color.WHITE);
        c.addView(t(a,18,true)); c.addView(t(b,13,false));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,6,0,6); c.setLayoutParams(p); return c;
    }

    private void module(String name,String sub,Runnable r){
        LinearLayout c=(LinearLayout)card(name,sub); c.setOnClickListener(v->r.run()); body.addView(c);
    }

    private double v(EditText e) throws Exception { return Double.parseDouble(e.getText().toString().trim()); }
    private String money(double x){ return String.format(Locale.US,"₹%,.0f",x); }
    private String pct(double x){ return String.format(Locale.US,"%+.2f%%",x); }
    private String f2(double x){ return String.format(Locale.US,"%.2f",x); }

    private void shareText(String subject,String text){
        Intent i=new Intent(Intent.ACTION_SEND); i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT,subject); i.putExtra(Intent.EXTRA_TEXT,text);
        startActivity(Intent.createChooser(i,"Share from Vama Wealth"));
    }

    private void showDashboard(){
        shell("Vama Wealth Intelligence – Core v1.0");
        body.addView(card("BUILD 5 • EXTENSIBLE CORE","Research • Compare • X-Ray • Simulate • Stress • Explain • Report • Share • Track • Extend"));
        if(enabled("cockpit")) module("Wealth Cockpit","One-screen portfolio health and scenario snapshot.",()->wealthCockpit());
        if(enabled("compare")) module("5-Year Fund Compare","Fund A vs Fund B vs Nifty with ROI, risk, drawdown and expense.",()->fundCompare());
        if(enabled("nifty")) module("NIFTY UP / DOWN MATRIX","-20% to +20% Nifty moves with portfolio ₹ and % impact.",()->niftyMatrix());
        if(enabled("xray")) module("Portfolio X-Ray","Top-5 concentration, HHI and underlying exposure proxy.",()->xray());
        if(enabled("twin")) module("Digital Twin","Current vs target allocation over a chosen horizon.",()->digitalTwin());
        if(enabled("goals")) module("Goals / SIP / Step-Up","SIP + lump sum + annual step-up illustration.",()->goals());
        if(enabled("impact")) module("Client Impact Radar","Translate a market move into one client portfolio impact.",()->clientImpact());
        if(enabled("market")) module("Market Cockpit","Nifty change, FII/DII, gold and USD/INR from entered verified values.",()->marketCockpit());
        if(enabled("changed")) module("What Changed?","Allocation, expense and AUM before/after detector.",()->whatChanged());
        if(enabled("evidence")) module("Evidence Mode","Source → Date → Calculation → Explanation.",()->evidence());
        if(enabled("reports")) module("Report Factory","Generate and share an advisor-ready text report.",()->reports());
        if(enabled("follow")) module("Lead & Follow-Up","Store a simple local follow-up record and count.",()->followup());
        if(enabled("share")) module("Share Studio","Create and share branded client communication text.",()->shareStudio());
        if(enabled("url")) module("Smart URL","Share report or website links from Android.",()->smartUrl());
        addCustomModulesToDashboard();
        module("Super Admin","Password-protected feature manager, competitor benchmark and module extension.",()->superAdmin());
        TextView n=t("Live NAV, AMC holdings and market feeds are not fabricated. Until verified feeds are connected, data-entry fields expect real values supplied by the user/advisor.",12,false);
        n.setTextColor(RED); body.addView(n);
    }

    private boolean enabled(String key){
        return prefs.getBoolean("feature_"+key,true);
    }

    private String hash(String value){
        try{
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            byte[] b=md.digest(value.getBytes("UTF-8"));
            StringBuilder out=new StringBuilder();
            for(byte x:b) out.append(String.format(Locale.US,"%02x",x & 0xff));
            return out.toString();
        }catch(Exception e){ return value; }
    }

    private EditText passwordInput(String hint){
        EditText e=txt(hint);
        e.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return e;
    }

    private void openWeb(String url){
        try{
            String u=url.trim();
            if(!u.startsWith("http://")&&!u.startsWith("https://"))u="https://"+u;
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));
        }catch(Exception ignored){}
    }

    private void addCustomModulesToDashboard(){
        String all=prefs.getString("custom_modules","");
        if(all.trim().length()==0)return;
        String[] rows=all.split("\\n");
        for(String row:rows){
            String[] p=row.split("\\|",-1);
            if(p.length<3)continue;
            final String name=p[0], desc=p[1], url=p[2];
            module(name,desc,()->openWeb(url));
        }
    }

    private void wealthCockpit(){
        shell("Wealth Cockpit"); back();
        EditText pv=num("Portfolio value ₹");
        EditText eq=num("Equity %");
        EditText debt=num("Debt %");
        EditText cash=num("Cash / other %");
        EditText nifty=num("Nifty scenario %");
        EditText ret=num("Assumed annual blended return %");
        TextView out=t("",16,false);
        Button go=btn("Generate cockpit");
        go.setOnClickListener(x->{try{
            double p=v(pv),e=v(eq),d=v(debt),c=v(cash),n=v(nifty),r=v(ret);
            if(e<0||d<0||c<0||Math.abs(e+d+c-100)>0.2)throw new Exception();
            double marketImpact=e*n/100.0;
            double scenario=p*(1+marketImpact/100.0);
            double oneYear=p*(1+r/100.0);
            out.setText("PORTFOLIO HEALTH SNAPSHOT\n\nEquity: "+money(p*e/100)+" ("+f2(e)+"%)\nDebt: "+money(p*d/100)+" ("+f2(d)+"%)\nCash/Other: "+money(p*c/100)+" ("+f2(c)+"%)\n\nNifty scenario: "+pct(n)+"\nEstimated portfolio impact: "+pct(marketImpact)+"\nScenario portfolio: "+money(scenario)+"\n\n1-year assumed value: "+money(oneYear)+"\nAssumption-based, not a forecast.");
        }catch(Exception e1){out.setText("Enter portfolio value and allocations totaling 100%.");}});
        body.addView(pv);body.addView(eq);body.addView(debt);body.addView(cash);body.addView(nifty);body.addView(ret);body.addView(go);body.addView(out);
    }

    private void fundCompare(){
        shell("5-Year Fund Compare"); back();
        body.addView(t("Enter verified metrics for Fund A, Fund B and Nifty. The app calculates the comparison; it does not invent live values.",13,false));
        EditText inv=num("Starting investment ₹");

        body.addView(t("Fund A",18,true));
        EditText an=txt("Fund A name"); EditText a1=num("A: 1Y return %"); EditText a3=num("A: 3Y CAGR %"); EditText a5=num("A: 5Y CAGR %");
        EditText av=num("A: volatility %"); EditText ad=num("A: max drawdown %"); EditText ae=num("A: expense ratio %");

        body.addView(t("Fund B",18,true));
        EditText bn=txt("Fund B name"); EditText b1=num("B: 1Y return %"); EditText b3=num("B: 3Y CAGR %"); EditText b5=num("B: 5Y CAGR %");
        EditText bv=num("B: volatility %"); EditText bd=num("B: max drawdown %"); EditText be=num("B: expense ratio %");

        body.addView(t("Nifty 50",18,true));
        EditText n1=num("Nifty: 1Y return %"); EditText n3=num("Nifty: 3Y CAGR %"); EditText n5=num("Nifty: 5Y CAGR %");

        TextView out=t("",15,false); Button go=btn("Compare all 5-year outcomes");
        go.setOnClickListener(x->{try{
            double p=v(inv),ar1=v(a1),ar3=v(a3),ar5=v(a5),avv=v(av),add=v(ad),aee=v(ae);
            double br1=v(b1),br3=v(b3),br5=v(b5),bvv=v(bv),bdd=v(bd),bee=v(be);
            double nr1=v(n1),nr3=v(n3),nr5=v(n5);
            String A=an.getText().toString().trim(); if(A.length()==0)A="Fund A";
            String B=bn.getText().toString().trim(); if(B.length()==0)B="Fund B";
            double af=p*Math.pow(1+ar5/100,5), bf=p*Math.pow(1+br5/100,5), nf=p*Math.pow(1+nr5/100,5);
            double aroi=(af/p-1)*100, broi=(bf/p-1)*100, nroi=(nf/p-1)*100;
            double aeff=avv==0?0:ar5/avv, beff=bvv==0?0:br5/bvv;
            String report=
                "5-YEAR EVIDENCE COMPARISON\n\n"+
                A+"\n1Y: "+f2(ar1)+"% | 3Y CAGR: "+f2(ar3)+"% | 5Y CAGR: "+f2(ar5)+"%\n5Y value: "+money(af)+" | cumulative ROI: "+f2(aroi)+"%\nVolatility: "+f2(avv)+"% | Drawdown: "+f2(add)+"% | Expense: "+f2(aee)+"%\nReturn/volatility: "+f2(aeff)+"\n\n"+
                B+"\n1Y: "+f2(br1)+"% | 3Y CAGR: "+f2(br3)+"% | 5Y CAGR: "+f2(br5)+"%\n5Y value: "+money(bf)+" | cumulative ROI: "+f2(broi)+"%\nVolatility: "+f2(bvv)+"% | Drawdown: "+f2(bdd)+"% | Expense: "+f2(bee)+"%\nReturn/volatility: "+f2(beff)+"\n\n"+
                "NIFTY 50\n1Y: "+f2(nr1)+"% | 3Y CAGR: "+f2(nr3)+"% | 5Y CAGR: "+f2(nr5)+"%\n5Y value: "+money(nf)+" | cumulative ROI: "+f2(nroi)+"%\n\n"+
                "RELATIVE TO NIFTY\n"+A+": "+money(af-nf)+"\n"+B+": "+money(bf-nf)+"\n\nNo automatic 'best fund' verdict; the evidence is shown for advisor/investor judgment.";
            out.setText(report);
        }catch(Exception e1){out.setText("Enter the investment and all comparison metrics.");}});
        Button share=btn("Share comparison");
        share.setOnClickListener(x->{String s=out.getText().toString(); if(s.length()>5)shareText("Vama 5-Year Comparison",s);});
        body.addView(inv);body.addView(an);body.addView(a1);body.addView(a3);body.addView(a5);body.addView(av);body.addView(ad);body.addView(ae);
        body.addView(bn);body.addView(b1);body.addView(b3);body.addView(b5);body.addView(bv);body.addView(bd);body.addView(be);
        body.addView(n1);body.addView(n3);body.addView(n5);body.addView(go);body.addView(share);body.addView(out);
    }

    private void niftyMatrix(){
        shell("NIFTY UP / DOWN MATRIX"); back();
        EditText pv=num("Portfolio value ₹");
        EditText eq=num("Equity allocation %");
        EditText beta=num("Portfolio equity beta to Nifty (example 1.0)");
        TextView out=t("",15,false); Button go=btn("Calculate full Nifty matrix");
        go.setOnClickListener(x->{try{
            double p=v(pv),e=v(eq),b=v(beta); if(e<0||e>100)throw new Exception();
            double[] moves={-20,-10,-5,5,10,20};
            StringBuilder s=new StringBuilder("NIFTY SCENARIO MATRIX\nAssumption: non-equity unchanged.\n\n");
            for(double m:moves){
                double imp=e/100.0*b*m;
                double value=p*(1+imp/100.0);
                s.append("Nifty ").append(pct(m)).append(" → Portfolio ").append(pct(imp)).append(" → ").append(money(value)).append("\n");
            }
            out.setText(s.toString());
        }catch(Exception e1){out.setText("Enter portfolio value, equity allocation and beta.");}});
        body.addView(pv);body.addView(eq);body.addView(beta);body.addView(go);body.addView(out);
    }

    private void xray(){
        shell("Portfolio X-Ray"); back();
        EditText pv=num("Portfolio value ₹");
        EditText w1=num("Largest holding %"); EditText w2=num("2nd holding %"); EditText w3=num("3rd holding %"); EditText w4=num("4th holding %"); EditText w5=num("5th holding %");
        TextView out=t("",15,false); Button go=btn("Run X-Ray");
        go.setOnClickListener(x->{try{
            double p=v(pv),a=v(w1),b=v(w2),c=v(w3),d=v(w4),e=v(w5);
            double top=a+b+c+d+e; if(top<0||top>100)throw new Exception();
            double other=100-top; double hhi=a*a+b*b+c*c+d*d+e*e+other*other;
            String flag=top>=70?"High top-5 concentration":(top>=50?"Moderate top-5 concentration":"Lower top-5 concentration");
            out.setText("Top-5 concentration: "+f2(top)+"%\nOther holdings combined: "+f2(other)+"%\nLargest holding value: "+money(p*a/100)+"\nApprox. HHI: "+f2(hhi)+"\nFlag: "+flag+"\n\nFull stock/sector/fund overlap requires imported verified holdings.");
        }catch(Exception e1){out.setText("Enter valid weights totaling 100% or less.");}});
        body.addView(pv);body.addView(w1);body.addView(w2);body.addView(w3);body.addView(w4);body.addView(w5);body.addView(go);body.addView(out);
    }

    private void digitalTwin(){
        shell("Digital Twin"); back();
        EditText p=num("Current portfolio ₹"), ce=num("Current equity %"), te=num("Target equity %"), er=num("Assumed equity return %"), dr=num("Assumed debt return %"), yrs=num("Years");
        TextView out=t("",16,false); Button go=btn("Simulate current vs target");
        go.setOnClickListener(x->{try{
            double pv=v(p),c=v(ce)/100,tg=v(te)/100,e=v(er)/100,d=v(dr)/100,y=v(yrs);
            if(c<0||c>1||tg<0||tg>1||y<0)throw new Exception();
            double cr=c*e+(1-c)*d, tr=tg*e+(1-tg)*d;
            double cv=pv*Math.pow(1+cr,y), tv=pv*Math.pow(1+tr,y);
            out.setText("Current allocation scenario: "+money(cv)+"\nTarget allocation scenario: "+money(tv)+"\nDifference: "+money(tv-cv)+"\nCurrent blended assumption: "+f2(cr*100)+"%\nTarget blended assumption: "+f2(tr*100)+"%\n\nScenario only; not a guaranteed return.");
        }catch(Exception e1){out.setText("Enter valid portfolio, allocations, assumptions and years.");}});
        body.addView(p);body.addView(ce);body.addView(te);body.addView(er);body.addView(dr);body.addView(yrs);body.addView(go);body.addView(out);
    }

    private void goals(){
        shell("Goals / SIP / Step-Up"); back();
        EditText sip=num("Starting monthly SIP ₹"), lump=num("Initial lump sum ₹"), step=num("Annual SIP step-up %"), years=num("Years"), rate=num("Assumed annual return %");
        TextView out=t("",16,true); Button go=btn("Calculate wealth illustration");
        go.setOnClickListener(x->{try{
            double monthly=v(sip), initial=v(lump), up=v(step)/100.0, y=v(years), annual=v(rate)/100.0;
            int months=(int)Math.round(y*12); double mr=annual/12.0; double value=initial;
            double contribution=initial;
            for(int m=0;m<months;m++){
                if(m>0 && m%12==0) monthly*=1+up;
                value=value*(1+mr)+monthly; contribution+=monthly;
            }
            out.setText("Illustrative future value: "+money(value)+"\nTotal contribution: "+money(contribution)+"\nIllustrative growth: "+money(value-contribution)+"\n\nAssumption only; not a return guarantee.");
        }catch(Exception e1){out.setText("Enter SIP, lump sum, step-up, years and assumed return.");}});
        body.addView(sip);body.addView(lump);body.addView(step);body.addView(years);body.addView(rate);body.addView(go);body.addView(out);
    }

    private void clientImpact(){
        shell("Client Impact Radar"); back();
        EditText name=txt("Client name"), pv=num("Client portfolio ₹"), eq=num("Equity %"), beta=num("Equity beta"), move=num("Nifty move %");
        TextView out=t("",16,false); Button go=btn("Calculate client impact");
        go.setOnClickListener(x->{try{
            String n=name.getText().toString().trim(); if(n.length()==0)n="Client";
            double p=v(pv),e=v(eq),b=v(beta),m=v(move); double imp=e/100*b*m; double val=p*(1+imp/100);
            out.setText(n+"\nNifty scenario: "+pct(m)+"\nEstimated portfolio impact: "+pct(imp)+"\nEstimated value: "+money(val)+"\nChange: "+money(val-p)+"\n\nScenario estimate based on entered beta/allocation.");
        }catch(Exception e1){out.setText("Enter client portfolio, equity %, beta and Nifty move.");}});
        body.addView(name);body.addView(pv);body.addView(eq);body.addView(beta);body.addView(move);body.addView(go);body.addView(out);
    }

    private void marketCockpit(){
        shell("Market Cockpit"); back();
        EditText nifty=num("Nifty current"), prev=num("Nifty previous close"), fii=num("FII flow ₹ crore"), dii=num("DII flow ₹ crore"), gold=num("Gold price"), usdinr=num("USD/INR");
        TextView out=t("",15,false); Button go=btn("Generate market snapshot");
        go.setOnClickListener(x->{try{
            double n=v(nifty),p=v(prev),f=v(fii),d=v(dii),g=v(gold),u=v(usdinr);
            double ch=p==0?0:(n/p-1)*100; double net=f+d;
            out.setText("MARKET SNAPSHOT\nNifty: "+f2(n)+" ("+pct(ch)+")\nFII: ₹"+f2(f)+" cr\nDII: ₹"+f2(d)+" cr\nNet institutional flow: ₹"+f2(net)+" cr\nGold: "+f2(g)+"\nUSD/INR: "+f2(u)+"\n\nAll values are user-entered; live feeds pending.");
        }catch(Exception e1){out.setText("Enter all market snapshot values.");}});
        body.addView(nifty);body.addView(prev);body.addView(fii);body.addView(dii);body.addView(gold);body.addView(usdinr);body.addView(go);body.addView(out);
    }

    private void whatChanged(){
        shell("What Changed?"); back();
        EditText oa=num("Previous allocation %"), na=num("Current allocation %"), oe=num("Previous expense %"), ne=num("Current expense %"), oldAum=num("Previous AUM ₹ crore"), newAum=num("Current AUM ₹ crore");
        TextView out=t("",16,false); Button go=btn("Detect changes");
        go.setOnClickListener(x->{try{
            double a=v(oa),b=v(na),c=v(oe),d=v(ne),e=v(oldAum),f=v(newAum);
            double aumPct=e==0?0:(f/e-1)*100;
            out.setText("Allocation: "+pct(b-a)+" pts\nExpense: "+pct(d-c)+" pts\nAUM: "+pct(aumPct)+"\nAUM absolute change: ₹"+f2(f-e)+" cr\n\nProduction mode can add holdings, sector, manager and benchmark changes from verified sources.");
        }catch(Exception e1){out.setText("Enter previous/current allocation, expense and AUM.");}});
        body.addView(oa);body.addView(na);body.addView(oe);body.addView(ne);body.addView(oldAum);body.addView(newAum);body.addView(go);body.addView(out);
    }

    private void evidence(){
        shell("Evidence Mode"); back();
        EditText q=txt("Finding / question"), src=txt("Source"), date=txt("Source date"), calc=txt("Calculation / method"), explain=txt("Explanation"), original=txt("Original data reference");
        TextView out=t("",15,false); Button go=btn("Create evidence block");
        go.setOnClickListener(x->{
            if(q.getText().toString().trim().length()==0){out.setText("Enter a finding or question.");return;}
            out.setText("FINDING\n"+q.getText()+"\n\nSOURCE\n"+src.getText()+"\nDATE\n"+date.getText()+"\nCALCULATION\n"+calc.getText()+"\nEXPLANATION\n"+explain.getText()+"\nORIGINAL DATA\n"+original.getText());
        });
        body.addView(q);body.addView(src);body.addView(date);body.addView(calc);body.addView(explain);body.addView(original);body.addView(go);body.addView(out);
    }

    private void reports(){
        shell("Report Factory"); back();
        EditText client=txt("Client / portfolio"), summary=txt("Research summary"), risk=txt("Risk / stress findings"), action=txt("Advisor follow-up / action"), evidence=txt("Evidence / source note");
        TextView out=t("",15,false); final String[] report={""};
        Button gen=btn("Generate report"); Button share=btn("Share report");
        gen.setOnClickListener(x->{
            report[0]="VAMA WEALTH INTELLIGENCE\nCLIENT DECISION REPORT\n\nClient/Portfolio: "+client.getText()+"\n\nResearch: "+summary.getText()+"\n\nRisk/Stress: "+risk.getText()+"\n\nFollow-up: "+action.getText()+"\n\nEvidence: "+evidence.getText()+"\n\nDisclosure: Research support and scenario analysis only; no guaranteed-return claim.";
            out.setText(report[0]);
        });
        share.setOnClickListener(x->{if(report[0].length()>5)shareText("Vama Wealth Report",report[0]);});
        body.addView(client);body.addView(summary);body.addView(risk);body.addView(action);body.addView(evidence);body.addView(gen);body.addView(share);body.addView(out);
    }

    private void followup(){
        shell("Lead & Follow-Up"); back();
        EditText name=txt("Client / lead name"), phone=txt("Phone / reference"), action=txt("Next action"), date=txt("Follow-up date");
        TextView out=t("",15,true); Button save=btn("Save local follow-up");
        save.setOnClickListener(x->{
            String n=name.getText().toString().trim();
            if(n.length()==0){out.setText("Enter a client / lead name.");return;}
            int count=prefs.getInt("follow_count",0)+1;
            String last=n+" | "+phone.getText()+" | "+action.getText()+" | "+date.getText();
            prefs.edit().putInt("follow_count",count).putString("last_follow",last).apply();
            out.setText("Saved locally\nFollow-up records added: "+count+"\nLast: "+last);
        });
        int c=prefs.getInt("follow_count",0); String last=prefs.getString("last_follow","None");
        out.setText("Follow-up records added: "+c+"\nLast: "+last);
        body.addView(name);body.addView(phone);body.addView(action);body.addView(date);body.addView(save);body.addView(out);
    }

    private void shareStudio(){
        shell("Share Studio"); back();
        EditText title=txt("Headline"), message=txt("Client message / market note");
        TextView out=t("",15,false); Button preview=btn("Preview branded text"), share=btn("Share");
        final String[] s={""};
        preview.setOnClickListener(x->{s[0]="VAMA WEALTH INTELLIGENCE\n"+title.getText()+"\n\n"+message.getText()+"\n\nResearch • X-Ray • Simulate • Explain • Report • Monitor";out.setText(s[0]);});
        share.setOnClickListener(x->{if(s[0].length()>5)shareText(title.getText().toString(),s[0]);});
        body.addView(title);body.addView(message);body.addView(preview);body.addView(share);body.addView(out);
    }

    private void smartUrl(){
        shell("Smart URL"); back();
        EditText url=txt("Paste report / website URL"); TextView out=t("",14,false); Button share=btn("Share URL");
        share.setOnClickListener(x->{String u=url.getText().toString().trim(); if(u.length()==0){out.setText("Enter a URL.");return;} shareText("Vama Wealth Link",u);});
        body.addView(url);body.addView(share);body.addView(t("Programmable short-link rules, QR creation and engagement analytics require the Vama backend. Android sharing is functional now.",13,false));body.addView(out);
    }

    private void superAdmin(){
        shell("Super Admin"); back();
        String stored=prefs.getString("admin_hash","");
        EditText pass=passwordInput(stored.length()==0?"Create Super Admin password":"Enter Super Admin password");
        TextView out=t("",14,false);
        Button enter=btn(stored.length()==0?"Create password & open admin":"Unlock Super Admin");
        enter.setOnClickListener(x->{
            String p=pass.getText().toString();
            if(p.length()<4){out.setText("Use at least 4 characters.");return;}
            String h=hash(p);
            String now=prefs.getString("admin_hash","");
            if(now.length()==0){
                prefs.edit().putString("admin_hash",h).apply();
                showAdminPanel();
            }else if(now.equals(h)){
                showAdminPanel();
            }else out.setText("Incorrect Super Admin password.");
        });
        body.addView(pass);body.addView(enter);body.addView(out);
    }

    private void showAdminPanel(){
        shell("Super Admin • Feature Manager"); back();
        body.addView(card("Version","Vama Wealth Intelligence Build 5 • Extensible Core • Android 6/API 23+"));
        body.addView(card("Extension model","Native modules can be enabled/disabled here. New web/Firebase/Railway modules can be added by URL without rebuilding the APK."));
        body.addView(card("Native-code limitation","A brand-new native Android capability still requires an APK update. Super Admin cannot safely create new compiled native code by password alone."));

        TextView status=t("Use the buttons below to switch native modules ON/OFF.",15,true);
        body.addView(status);
        String[][] features={
            {"cockpit","Wealth Cockpit"},{"compare","5-Year Fund Compare"},{"nifty","Nifty Matrix"},{"xray","Portfolio X-Ray"},
            {"twin","Digital Twin"},{"goals","Goals / SIP / Step-Up"},{"impact","Client Impact Radar"},{"market","Market Cockpit"},
            {"changed","What Changed?"},{"evidence","Evidence Mode"},{"reports","Report Factory"},{"follow","Lead & Follow-Up"},
            {"share","Share Studio"},{"url","Smart URL"}
        };
        for(String[] f:features){
            final String key=f[0], label=f[1];
            Button b=btn(label+" — "+(enabled(key)?"ON":"OFF"));
            b.setOnClickListener(v->{
                boolean next=!enabled(key);
                prefs.edit().putBoolean("feature_"+key,next).apply();
                showAdminPanel();
            });
            body.addView(b);
        }

        body.addView(t("ADD NEW MODULE WITHOUT APK REBUILD",18,true));
        EditText name=txt("Module name");
        EditText desc=txt("Short description");
        EditText url=txt("HTTPS URL / Firebase / Railway page");
        TextView moduleOut=t("",14,false);
        Button add=btn("Add module to dashboard");
        add.setOnClickListener(v->{
            String n=name.getText().toString().replace("|","/").replace("\n"," ").trim();
            String d=desc.getText().toString().replace("|","/").replace("\n"," ").trim();
            String u=url.getText().toString().replace("|","").replace("\n","").trim();
            if(n.length()==0||u.length()==0){moduleOut.setText("Enter module name and URL.");return;}
            String row=n+"|"+d+"|"+u;
            String all=prefs.getString("custom_modules","");
            if(all.length()>0)all+="\n";
            prefs.edit().putString("custom_modules",all+row).apply();
            moduleOut.setText("Module added. Return to Dashboard to use it.");
        });
        Button clear=btn("Remove all custom modules");
        clear.setOnClickListener(v->{prefs.edit().remove("custom_modules").apply();moduleOut.setText("Custom modules removed.");});
        body.addView(name);body.addView(desc);body.addView(url);body.addView(add);body.addView(clear);body.addView(moduleOut);

        body.addView(t("COMPETITIVE BENCHMARK • FELIX PUBLIC FEATURE SNAPSHOT",18,true));
        body.addView(card("Fund research / comparison","Felix: publicly advertises live research, screening and comparison. Vama: 5-year ROI/risk comparison; verified live feeds still pending."));
        body.addView(card("AI / portfolio building","Felix: AI portfolio builder and chat. Vama: Digital Twin, Nifty stress matrix, Client Impact Radar and Evidence Mode; live AI/data integration pending."));
        body.addView(card("Market terminal","Felix: live market terminal. Vama: Market Cockpit and portfolio-impact engine; live feeds pending."));
        body.addView(card("Clients / leads","Felix: client book and lead tools. Vama: local lead/follow-up workflow now; shared CRM/backend pending."));
        body.addView(card("Reports / proposals","Felix: branded proposals/reports. Vama: Report Factory and sharing now; automated PDF proposal layer pending."));
        body.addView(card("Content / sharing","Felix: Share Kit/newsletters. Vama: Share Studio and Smart URL now; automated content/newsletter engine pending."));
        body.addView(card("Vama depth modules","Portfolio X-Ray • Digital Twin • Nifty stress matrix • Client Impact Radar • Evidence Mode • What Changed?"));
        body.addView(t("Benchmark is based on publicly advertised capability categories and should be refreshed when Felix changes.",12,false));

        Button change=btn("Change Super Admin password");
        change.setOnClickListener(v->{prefs.edit().remove("admin_hash").apply();superAdmin();});
        Button reset=btn("Reset all local Vama data");
        TextView out=t("",14,false);
        reset.setOnClickListener(v->{prefs.edit().clear().apply();out.setText("All local settings, follow-ups, modules and password reset.");});
        body.addView(change);body.addView(reset);body.addView(out);
    }

    @Override public void onBackPressed(){ showDashboard(); }
}
