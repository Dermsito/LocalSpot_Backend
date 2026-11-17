/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9928790199081163, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "E7_2_Create_Categoria"], "isController": false}, {"data": [1.0, 500, 1500, "GET_02_Lugares"], "isController": false}, {"data": [1.0, 500, 1500, "POST_05_Fase4_300"], "isController": false}, {"data": [1.0, 500, 1500, "E4_5_Consulta_Final"], "isController": false}, {"data": [1.0, 500, 1500, "POST_05_Fase3_200"], "isController": false}, {"data": [1.0, 500, 1500, "E7_4_Delete_Categoria"], "isController": false}, {"data": [1.0, 500, 1500, "E4_4_Actualizacion_Categoria"], "isController": false}, {"data": [0.98998998998999, 500, 1500, "E7_1_Login"], "isController": false}, {"data": [1.0, 500, 1500, "GET_06_Filtro_Complejo"], "isController": false}, {"data": [1.0, 500, 1500, "E4_2_Consulta"], "isController": false}, {"data": [1.0, 500, 1500, "E4_1_Login"], "isController": false}, {"data": [1.0, 500, 1500, "POST_05_Fase2_100"], "isController": false}, {"data": [1.0, 500, 1500, "POST_01_Login"], "isController": false}, {"data": [0.0875, 500, 1500, "POST_03_Register"], "isController": false}, {"data": [1.0, 500, 1500, "E4_3_Registro_Categoria"], "isController": false}, {"data": [1.0, 500, 1500, "POST_05_Fase1_50"], "isController": false}, {"data": [1.0, 500, 1500, "E7_3_Update_Categoria"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 6530, 0, 0.0, 103.15604900459405, 3, 8738, 55.0, 227.0, 300.4499999999998, 487.7599999999984, 45.37684321710005, 53.468605374341585, 16.87609473588662], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["E7_2_Create_Categoria", 997, 0, 0.0, 50.48445336008022, 6, 238, 43.0, 100.0, 122.09999999999991, 167.0999999999999, 42.12082805238699, 22.410397193177015, 22.286996329742287], "isController": false}, {"data": ["GET_02_Lugares", 800, 0, 0.0, 13.008750000000006, 8, 35, 12.0, 18.0, 20.0, 26.99000000000001, 53.47951066247744, 261.0781970719968, 10.758573434053078], "isController": false}, {"data": ["POST_05_Fase4_300", 300, 0, 0.0, 76.92666666666669, 61, 163, 75.0, 91.90000000000003, 100.0, 137.97000000000003, 14.927601134497685, 11.720499328257949, 3.9651440513509475], "isController": false}, {"data": ["E4_5_Consulta_Final", 100, 0, 0.0, 5.250000000000001, 3, 10, 5.0, 7.0, 8.0, 9.989999999999995, 10.15950421619425, 10.764709057198008, 1.9247498222086763], "isController": false}, {"data": ["POST_05_Fase3_200", 200, 0, 0.0, 75.28499999999997, 61, 144, 73.0, 87.9, 92.0, 139.6600000000003, 13.324450366422385, 10.461775483011326, 3.539307128580946], "isController": false}, {"data": ["E7_4_Delete_Categoria", 997, 0, 0.0, 55.639919759277774, 7, 302, 46.0, 109.0, 134.0999999999999, 195.1199999999999, 42.115490220926795, 15.957822466523044, 19.86502126631606], "isController": false}, {"data": ["E4_4_Actualizacion_Categoria", 100, 0, 0.0, 9.26, 6, 16, 9.0, 12.0, 13.0, 15.969999999999985, 10.151253679829459, 5.1405591754644195, 5.150472509136128], "isController": false}, {"data": ["E7_1_Login", 999, 0, 0.0, 259.33133133133134, 74, 710, 247.0, 385.0, 434.0, 577.0, 41.93778598715419, 33.828721899794296, 11.262589010222072], "isController": false}, {"data": ["GET_06_Filtro_Complejo", 300, 0, 0.0, 6.11666666666667, 4, 19, 6.0, 8.0, 9.0, 10.990000000000009, 14.97155404731011, 14.094314552350534, 3.77212982832618], "isController": false}, {"data": ["E4_2_Consulta", 100, 0, 0.0, 8.530000000000001, 6, 16, 8.0, 11.0, 12.0, 15.969999999999985, 10.144045445323595, 27.10362142422398, 2.0307903479407585], "isController": false}, {"data": ["E4_1_Login", 100, 0, 0.0, 78.44000000000003, 64, 123, 76.0, 98.9, 102.0, 122.95999999999998, 10.034115994380896, 8.093925597029902, 2.6947088852097134], "isController": false}, {"data": ["POST_05_Fase2_100", 100, 0, 0.0, 75.11999999999996, 61, 130, 73.0, 89.0, 92.89999999999998, 129.8099999999999, 10.030090270812437, 7.875188064192577, 2.6642427281845533], "isController": false}, {"data": ["POST_01_Login", 250, 0, 0.0, 120.14000000000001, 68, 300, 111.0, 165.8, 228.34999999999997, 251.45000000000005, 25.025025025025027, 19.648554804804803, 6.647272272272272], "isController": false}, {"data": ["POST_03_Register", 40, 0, 0.0, 3710.225000000001, 1415, 8738, 3182.5, 6654.499999999999, 8523.499999999998, 8738.0, 2.66844563042028, 1.6512310290193462, 0.8671796822881921], "isController": false}, {"data": ["E4_3_Registro_Categoria", 100, 0, 0.0, 8.050000000000004, 5, 12, 8.0, 10.0, 11.0, 12.0, 10.147133434804667, 5.4258426877219685, 5.396114757737189], "isController": false}, {"data": ["POST_05_Fase1_50", 50, 0, 0.0, 75.02000000000004, 62, 128, 72.5, 85.9, 111.59999999999997, 128.0, 10.04217714400482, 7.884678148222535, 2.66745330387628], "isController": false}, {"data": ["E7_3_Update_Categoria", 997, 0, 0.0, 57.86760280842528, 7, 340, 49.0, 114.0, 133.0, 183.01999999999998, 42.11726934775262, 21.25685969975076, 21.297989845598174], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 6530, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
