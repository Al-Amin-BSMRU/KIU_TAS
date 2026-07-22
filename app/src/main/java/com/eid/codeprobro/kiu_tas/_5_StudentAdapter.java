package com.eid.codeprobro.kiu_tas;

import static java.security.AccessController.getContext;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class _5_StudentAdapter extends RecyclerView.Adapter<_5_StudentAdapter.StudentViewHolder> {

    private List<_5_StudentModel> studentList;

    public _5_StudentAdapter(List<_5_StudentModel> studentList) {

        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater lf=LayoutInflater.from(parent.getContext());
        View v = lf.inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        _5_StudentModel student = studentList.get(position);
        holder.tvStudentName.setText(student.getName());
        holder.tvStudentId.setText("ID: " + student.getId());

        if (student.isPresent()) {
            holder.viewPresenceIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.viewPresenceIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {

        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentId;
        View viewPresenceIndicator;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            viewPresenceIndicator = itemView.findViewById(R.id.viewPresenceIndicator);
        }
    }
}
