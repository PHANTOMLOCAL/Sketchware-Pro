package com.besome.sketch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.besome.sketch.design.DesignActivity;
import com.besome.sketch.editor.manage.library.ProjectComparator;
import com.besome.sketch.projects.MyProjectSettingActivity;
import com.sketchware.remod.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import a.a.a.DA;
import a.a.a.DB;
import a.a.a.lC;
import a.a.a.wB;
import mod.hasrat.dialog.SketchDialog;
import mod.hey.studios.project.ProjectTracker;
import mod.hey.studios.project.backup.BackupRestoreManager;
import mod.hey.studios.util.Helper;

public class ProjectsFragment extends DA implements View.OnClickListener {
    private static final int REQUEST_CODE_RESTORE_PROJECT = 700;
    private static final int REQUEST_CODE_DESIGN_ACTIVITY = 204;
    public static final int REQUEST_CODE_PROJECT_SETTINGS_ACTIVITY = 206;

    private SwipeRefreshLayout swipeRefresh;
    private SearchView projectsSearchView;
    private RecyclerView myProjects;
    private final ArrayList<HashMap<String, Object>> projectsList = new ArrayList<>();
    private ProjectsAdapter projectsAdapter;
    private DB preference;

    private void initialize(ViewGroup parent) {
        preference = new DB(requireContext(), "project");
        swipeRefresh = parent.findViewById(R.id.swipe_refresh);

        requireActivity().findViewById(R.id.create_new_project).setOnClickListener(this);

        swipeRefresh.setOnRefreshListener(() -> {
            // Check storage access
            if (!c()) {
                // Ask for it
                ((MainActivity) requireActivity()).s();
            } else {
                refreshProjectsList();
            }
        });

        myProjects = parent.findViewById(R.id.myprojects);
        myProjects.setHasFixedSize(true);

        refreshProjectsList();
    }

    public void refreshProjectsList() {
        // Don't load project list without having permissions
        if (!c()) return;

        new Thread(() -> {
            synchronized (projectsList) {
                projectsList.clear();
                projectsList.addAll(lC.a());
                Collections.sort(projectsList, new ProjectComparator(preference.d("sortBy")));
            }

            requireActivity().runOnUiThread(() -> {
                if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
                projectsAdapter = new ProjectsAdapter(getActivity(), new ArrayList<>(projectsList));
                myProjects.setAdapter(projectsAdapter);
                if (projectsSearchView != null)
                    projectsAdapter.filterData(projectsSearchView.getQuery().toString());
            });
        }).start();
    }

    @Override
    public void b(int requestCode) {
        if (requestCode == REQUEST_CODE_PROJECT_SETTINGS_ACTIVITY) {
            toProjectSettingsActivity();
        } else if (requestCode == REQUEST_CODE_RESTORE_PROJECT) {
            restoreProject();
        }
    }

    public static void toDesignActivity(Activity activity, String sc_id) {
        Intent intent = new Intent(activity, DesignActivity.class);
        ProjectTracker.setScId(sc_id);
        intent.putExtra("sc_id", sc_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityForResult(intent, REQUEST_CODE_DESIGN_ACTIVITY);
    }

    @Override
    public void c(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void d() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).s();
        }
    }

    @Override
    public void e() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).s();
        }
    }

    public int getProjectsCount() {
        synchronized (projectsList) {
            return projectsList.size();
        }
    }

    private void toProjectSettingsActivity() {
        Intent intent = new Intent(getActivity(), MyProjectSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_PROJECT_SETTINGS_ACTIVITY);
    }

    private void restoreProject() {
        (new BackupRestoreManager(getActivity(), this)).restore();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROJECT_SETTINGS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                refreshProjectsList();
                if (data.getBooleanExtra("is_new", false)) {
                    toDesignActivity(getActivity(), data.getStringExtra("sc_id"));
                }
            }
        } else if (requestCode == REQUEST_CODE_RESTORE_PROJECT) {
            if (resultCode == Activity.RESULT_OK) {
                refreshProjectsList();
                restoreProject();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if ((viewId == R.id.create_new_project) && super.a(REQUEST_CODE_PROJECT_SETTINGS_ACTIVITY)) {
            final String[] actions = {"Create New Project", "Restore Project (.swb)"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose an action");
            builder.setItems(actions, (dialog, which) -> {
                if (which == 0) toProjectSettingsActivity();
                else restoreProject();
            });
            builder.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.clear();
        menuInflater.inflate(R.menu.projects_fragment_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        projectsSearchView = (SearchView) menu.findItem(R.id.searchProjects).getActionView();
        projectsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                projectsAdapter.filterData(s);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sortProject) showProjectSortingDialog();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.myprojects, parent, false);
        setHasOptionsMenu(true);
        initialize(viewGroup);
        return viewGroup;
    }

    private void showProjectSortingDialog() {
        SketchDialog dialog = new SketchDialog(requireActivity());
        dialog.setTitle("Sort options");
        View root = wB.a(requireActivity(), R.layout.sort_project_dialog);
        RadioButton sortByName = root.findViewById(R.id.sortByName);
        RadioButton sortByID = root.findViewById(R.id.sortByID);
        RadioButton sortOrderAsc = root.findViewById(R.id.sortOrderAsc);
        RadioButton sortOrderDesc = root.findViewById(R.id.sortOrderDesc);

        int storedValue = preference.a("sortBy", ProjectComparator.DEFAULT);
        if ((storedValue & ProjectComparator.SORT_BY_NAME) == ProjectComparator.SORT_BY_NAME) {
            sortByName.setChecked(true);
        }
        if ((storedValue & ProjectComparator.SORT_BY_ID) == ProjectComparator.SORT_BY_ID) {
            sortByID.setChecked(true);
        }
        if ((storedValue & ProjectComparator.SORT_ORDER_ASCENDING) == ProjectComparator.SORT_ORDER_ASCENDING) {
            sortOrderAsc.setChecked(true);
        }
        if ((storedValue & ProjectComparator.SORT_ORDER_DESCENDING) == ProjectComparator.SORT_ORDER_DESCENDING) {
            sortOrderDesc.setChecked(true);
        }
        dialog.setView(root);
        dialog.setPositiveButton("Save", v -> {
            int sortValue = 0;
            if (sortByName.isChecked()) {
                sortValue |= ProjectComparator.SORT_BY_NAME;
            }
            if (sortByID.isChecked()) {
                sortValue |= ProjectComparator.SORT_BY_ID;
            }
            if (sortOrderAsc.isChecked()) {
                sortValue |= ProjectComparator.SORT_ORDER_ASCENDING;
            }
            if (sortOrderDesc.isChecked()) {
                sortValue |= ProjectComparator.SORT_ORDER_DESCENDING;
            }
            preference.a("sortBy", sortValue, true);
            dialog.dismiss();
            refreshProjectsList();
        });
        dialog.setNegativeButton("Cancel", Helper.getDialogDismissListener(dialog));
        dialog.show();
    }
}
