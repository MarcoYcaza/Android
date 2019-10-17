package com.example.tdp_cell_master2;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.tdp_cell_master2.R.*;


public class MainActivity extends AppCompatActivity {

    private final static long REPETITIVE_TASK_DELAY_IN_MILLIS = 10000;


    //______________Variables_________________

    private RepetitiveTask cellRepetitiveTask;
    private TextView network_details;
    private String btsNameFetched;
    private TextView netText;
    private TextView mTextoResultado;

    private Integer cellId;
    private Integer area_code;
    private Integer pci;
    private Integer psc;


    private CellRegistered cellRegistered;


    private bts_view_model bts_viewModel;
    private CellParameterGetter cell_monitor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

         network_details =    findViewById(id.detailsNetTx);
         mTextoResultado = findViewById(id.textRes);
         netText = findViewById(id.netTypeTx);

         cell_monitor = new CellParameterGetter(getApplication());


         cellId = cell_monitor.action_monitor().getCid();
         area_code= cell_monitor.action_monitor().getLac();
         pci= cell_monitor.action_monitor().getPci();
         psc= cell_monitor.action_monitor().getPsc();

         bts_viewModel = ViewModelProviders.of(this).get(bts_view_model.class);


         // Tarea repetitiva.
        cellRepetitiveTask =    createCellRepetitiveTask();
        cellRepetitiveTask.start(true);


    }

    public void WorkingCellTasks() {

        cellRegistered = cell_monitor.action_monitor();

        cellId = cellRegistered.getCid();
        area_code = cellRegistered.getLac();
        pci = cellRegistered.getPci();
        psc = cellRegistered.getPsc();


        switch (cellRegistered.getType())
        {
            case "GSM":
                netText.setText(getString(string.GSM));
                final String fill_text_G = "Cell ID: " + cellId +
                        System.getProperty ("line.separator")+
                        "LAC: " + area_code ;
                network_details.setText(fill_text_G);

                break;
            case "WCDMA":
                netText.setText(getString(string.WCDMA));
                final String fill_text_W = "Cell ID: " + cellId +
                        System.getProperty ("line.separator")+
                        "LAC: " + area_code+
                        System.getProperty ("line.separator")+
                        "PSC: " + psc ;

                network_details.setText(fill_text_W);

                break;
            case "LTE":
                netText.setText(string.LTE);

                final String fill_text_L = "enodeB ID: " + cellId +
                        System.getProperty ("line.separator")+
                        "TAC: " + area_code+
                        System.getProperty ("line.separator")+
                        "PCI: " + pci ;

                network_details.setText(fill_text_L);

                break;
            case "UNKNOWN":
                network_details.setText(string.NONE);
                break;
            default:
                break;
        }

        Toast.makeText(this, ""+cellRegistered.getCid(), Toast.LENGTH_SHORT).show();

    }

    private RepetitiveTask createCellRepetitiveTask() {

        return new RepetitiveTask(new Runnable() {

                @Override
                public void run() {

                    WorkingCellTasks();

//                    final Handler handler = new Handler();
//
//                    btsNameFetched = bts_viewModel.FindThing(cellRegistered.getCid(),handler);
//
//                    mTextoResultado.setText(btsNameFetched);

                }

            }, REPETITIVE_TASK_DELAY_IN_MILLIS);

    }

}
